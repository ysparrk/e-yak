package now.eyak.prescription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.MedicineRoutineUpdateDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import now.eyak.routine.repository.PrescriptionMedicineRoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MemberRepository memberRepository;
    private final MedicineRoutineRepository medicineRoutineRepository;
    private final PrescriptionMedicineRoutineRepository prescriptionMedicineRoutineRepository;
    private final MedicineRoutineCheckRepository medicineRoutineCheckRepository;


    /**
     * 복약 정보를 등록한다.
     * @param prescriptionDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription insert(PrescriptionDto prescriptionDto, Long memberId) {
        Prescription prescription = prescriptionDto.toEntity();
        log.debug("prescriptionDto = {}", prescriptionDto);
        Member member = getMemberOrThrow(memberId);
        prescription.setMember(member);

        prescriptionDto.getMedicineRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .medicineRoutine(medicineRoutine)
                    .prescription(prescription)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // 복약 등록하면 MedicineCheck 테이블 생성
        List<PrescriptionMedicineRoutine> allRoutines = prescriptionMedicineRoutineRepository.findByPrescription(prescription);

        LocalTime now = LocalTime.now(); // 등록 시간
        LocalTime eatingDuration = member.getEatingDuration();  // 식사 시간

        List<LocalTime> times = new ArrayList<>();
        times.add(member.getWakeTime());
        times.add(member.getBreakfastTime());
        times.add(member.getBreakfastTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getLunchTime());
        times.add(member.getLunchTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getDinnerTime());
        times.add(member.getDinnerTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getBedTime());
        times.add(now);
        Collections.sort(times);

        int now_idx = times.indexOf(now);  // 등록 시간의 idx

        // routine 리스트
        List<Routine> routines = new ArrayList<>();
        routines.addAll(Arrays.stream(Routine.values()).toList());


        for (PrescriptionMedicineRoutine prescriptionMedicineRoutine : allRoutines) {
            if (routines.indexOf(prescriptionMedicineRoutine.getMedicineRoutine().getRoutine()) <= now_idx) continue;

            MedicineRoutineCheck medicineRoutineCheck = MedicineRoutineCheck.builder()
                    .date(LocalDate.now())
                    .medicineRoutine(prescriptionMedicineRoutine.getMedicineRoutine())
                    .prescription(prescription)
                    .took(false) // 초기 값 false
                    .member(member)
                    .build();
            medicineRoutineCheckRepository.save(medicineRoutineCheck);

        }

        return savedPrescription;
    }

    @Transactional
    @Override
    public List<Prescription> findAllByMemberId(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return prescriptionRepository.findAllByMember(member);
    }

    /**
     * 사용자(memberId)의 복약 정보 중 dateTime에 복용해야하는 복약 정보를 반환한다.
     *
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime) {
        Member member = getMemberOrThrow(memberId);

        return prescriptionRepository.findAllByMemberAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(member, dateTime, dateTime);
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 반환한다.
     *
     * @param prescriptionId
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription findById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 수정한다.
     *
     * @param prescriptionId
     * @param prescriptionDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionAndCheckPermission(prescriptionId, member);

        prescriptionDto.update(prescription);

        prescription.getPrescriptionMedicineRoutines().clear();
        prescriptionDto.getMedicineRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .prescription(prescription)
                    .medicineRoutine(medicineRoutine)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        return prescriptionRepository.save(prescription);
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 삭제한다.
     *
     * @param prescriptionId
     * @param memberId
     */
    @Transactional
    @Override
    public void delete(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        prescriptionRepository.deleteByIdAndMember(prescriptionId, member);
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)의 복약 루틴을 반환한다.
     *
     * @param prescriptionId
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<PrescriptionMedicineRoutine> findPrescriptionMedicineRoutinesById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescriptionMedicineRoutineRepository.findByPrescription(prescription);
    }

    @Transactional
    @Override
    public List<PrescriptionMedicineRoutine> updatePrescriptionMedicineRoutinesById(MedicineRoutineUpdateDto medicineRoutineUpdateDto, Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        prescription.getPrescriptionMedicineRoutines().clear();
        medicineRoutineUpdateDto.getRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .prescription(prescription)
                    .medicineRoutine(medicineRoutine)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        return prescriptionRepository.save(prescription).getPrescriptionMedicineRoutines();
    }

    private Prescription getPrescriptionAndCheckPermission(Long prescriptionId, Member member) {
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    private Prescription getPrescriptionByIdAndMemberOrThrow(Long prescriptionId, Member member) {
        Prescription prescription = prescriptionRepository.findByIdAndMember(prescriptionId, member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당 Prescription은 존재하지 않습니다."));
        return prescription;
    }

    private Member getMemberOrThrow(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        return member;
    }


}
