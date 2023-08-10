package now.eyak.prescription.service;

import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.MedicineRoutineUpdateDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;

import java.time.LocalDateTime;
import java.util.List;

public interface PrescriptionService {
    Prescription insert(PrescriptionDto prescriptionDto, Long memberId);
    List<Prescription> findAllByMemberId(Long memberId);
    List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime); // 당일 복용해야 할 전체 조회
    PrescriptionResponseDto findAllAndSortWithRoutine(Long memberId, LocalDateTime dateTime);  // 복약 루틴에 따라 분류(당일)
    PrescriptionResponseDto findAllAndSortWithRoutineFuture(Long memberId, LocalDateTime dateTime); // 복약 루틴에 따라 분류(미래)
    Prescription findById(Long prescriptionId, Long memberId);
    Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId);
    void delete(Long prescriptionId, Long memberId);
    List<PrescriptionMedicineRoutine> findPrescriptionMedicineRoutinesById(Long prescriptionId, Long memberId);
    List<PrescriptionMedicineRoutine> updatePrescriptionMedicineRoutinesById(MedicineRoutineUpdateDto medicineRoutineUpdateDto, Long prescriptionId, Long memberId);
}
