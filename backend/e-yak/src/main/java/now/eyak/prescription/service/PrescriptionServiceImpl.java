package now.eyak.prescription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.repository.MedicineRoutineRepository;
import now.eyak.routine.repository.PrescriptionMedicineRoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MemberRepository memberRepository;
    private final MedicineRoutineRepository medicineRoutineRepository;
    private final PrescriptionMedicineRoutineRepository prescriptionMedicineRoutineRepository;

    @Transactional
    @Override
    public Prescription insert(PrescriptionDto prescriptionDto, Long memberId) {
        Prescription prescription = prescriptionDto.toEntity();
        log.debug("prescriptionDto = {}", prescriptionDto);
        Member member = getMemberOrThrow(memberId);
        prescription.setMember(member);

        prescriptionDto.getRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .medicineRoutine(medicineRoutine)
                    .prescription(prescription)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        return savedPrescription;
    }

    @Transactional
    @Override
    public List<Prescription> findAllByMemberId(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return prescriptionRepository.findAllByMember(member);
    }

    @Override
    public List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime) {
        Member member = getMemberOrThrow(memberId);

        return prescriptionRepository.findAllByMemberAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(member, dateTime, dateTime);
    }

    @Transactional
    @Override
    public Prescription findById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    @Transactional
    @Override
    public Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionAndCheckPermission(prescriptionId, member);

        prescriptionDto.update(prescription);

        prescription.getPrescriptionMedicineRoutines().clear();
        prescriptionDto.getRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .prescription(prescription)
                    .medicineRoutine(medicineRoutine)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        return prescriptionRepository.save(prescription);
    }

    @Transactional
    @Override
    public void delete(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

//        Prescription prescription = prescriptionRepository.findByIdAndMember(prescriptionId, member).orElseThrow(() -> new NoSuchElementException("해당 하는 복약 정보가 존재하지 않습니다."));
        prescriptionRepository.deleteByIdAndMember(prescriptionId, member);

//        log.debug("prescription: {}", prescription.getPrescriptionMedicineRoutines());
    }

    @Override
    public List<PrescriptionMedicineRoutine> findPrescriptionMedicineRoutinesById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescriptionMedicineRoutineRepository.findByPrescription(prescription);
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
