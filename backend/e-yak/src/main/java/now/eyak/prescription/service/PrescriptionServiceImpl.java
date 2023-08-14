package now.eyak.prescription.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.MedicineRoutineUpdateDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.prescription.dto.query.PrescriptionRoutineQueryDto;
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
import java.time.ZonedDateTime;
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

    private final JPAQueryFactory queryFactory;

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

        ZonedDateTime createdAt = savedPrescription.getCreatedAt();
        LocalTime createdTime = createdAt.toLocalTime(); // 등록 시간
        LocalTime eatingDuration = member.getEatingDuration();  // 식사 시간

        List<LocalTime> times = new ArrayList<>();
        times.add(member.getWakeTime());
        times.add(member.getBreakfastTime());
        times.add(member.getBreakfastTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getLunchTime());
        times.add(member.getLunchTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getDinnerTime());
        times.add(member.getDinnerTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getBedTime()); // TODO: BED_TIME 12시 이후 일 경우 고려해서 로직 작성
        times.add(createdTime);
        Collections.sort(times);

        int createdIdx = times.indexOf(createdTime);  // 등록 시간의 idx

        // routine 리스트
        List<Routine> routines = new ArrayList<>();
        routines.addAll(Arrays.stream(Routine.values()).toList());


        for (PrescriptionMedicineRoutine prescriptionMedicineRoutine : allRoutines) {
            if (routines.indexOf(prescriptionMedicineRoutine.getMedicineRoutine().getRoutine()) < createdIdx) continue;

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
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime) {
        Member member = getMemberOrThrow(memberId);

        return prescriptionRepository.findAllByMemberAndBetweenStartAndEndDateTime(member, dateTime);
    }


    /**
     * 사용자(memberId)의 복약 정보 중 dateTime에 복용해야하는 복약 정보를 반환한다.
     * 루틴에 대해 리스트업 해서 전달하기
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public PrescriptionResponseDto findAllAndSortWithRoutine(Long memberId, LocalDateTime dateTime) {
        /**
         * 1. 요청받은 member
         * 2. 요청받은 date에 대해서 startDate ~ endDate
         * 3. 요청받은 date와 startDate가 같다면, 루틴에 대한 필터링이 필요
         * 4. 루틴별로 처방전 id 넣기
         */

        Member member = getMemberOrThrow(memberId);

        Routine[] routines = Routine.values();
        List<List<PrescriptionRoutineQueryDto>> prescriptionRoutineQueryDtoList = new ArrayList<>();
        for (Routine routine : routines) {
            List<PrescriptionRoutineQueryDto> byRoutine = prescriptionRepository.findByRoutine(routine, member, dateTime);
            prescriptionRoutineQueryDtoList.add(byRoutine);
        }

        // responseDto에 리스트 넣기
        PrescriptionResponseDto prescriptionResponseDto = PrescriptionResponseDto.builder()
                .bedAfterQueryResponses(prescriptionRoutineQueryDtoList.get(0))
                .breakfastBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(1))
                .breakfastAfterQueryResponses(prescriptionRoutineQueryDtoList.get(2))
                .lunchBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(3))
                .lunchAfterQueryResponses(prescriptionRoutineQueryDtoList.get(4))
                .dinnerBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(5))
                .dinnerAfterQueryResponses(prescriptionRoutineQueryDtoList.get(6))
                .bedBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(7))
                .build();

        return prescriptionResponseDto;

    }

    /**
     * 사용자(memberId)의 복약 정보 중 dateTime에 복용해야하는 복약 정보를 반환(미래 날짜에 대해)
     * endDate의 경우, 필터링 필요
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public PrescriptionResponseDto findAllAndSortWithRoutineFuture(Long memberId, LocalDateTime dateTime) {
        Member member = getMemberOrThrow(memberId);

        Routine[] routines = Routine.values();
        List<List<PrescriptionRoutineQueryDto>> prescriptionRoutineQueryDtoList = new ArrayList<>();
        for (Routine routine : routines) {
            List<PrescriptionRoutineQueryDto> byRoutine = prescriptionRepository.findByRoutineForFuture(routine, member, dateTime);
            prescriptionRoutineQueryDtoList.add(byRoutine);
        }

        PrescriptionResponseDto prescriptionResponseDto = PrescriptionResponseDto.builder()
                .bedAfterQueryResponses(prescriptionRoutineQueryDtoList.get(0))
                .breakfastBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(1))
                .breakfastAfterQueryResponses(prescriptionRoutineQueryDtoList.get(2))
                .lunchBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(3))
                .lunchAfterQueryResponses(prescriptionRoutineQueryDtoList.get(4))
                .dinnerBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(5))
                .dinnerAfterQueryResponses(prescriptionRoutineQueryDtoList.get(6))
                .bedBeforeQueryResponses(prescriptionRoutineQueryDtoList.get(7))
                .build();

        return prescriptionResponseDto;
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
