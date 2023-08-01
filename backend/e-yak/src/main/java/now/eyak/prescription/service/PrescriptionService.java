package now.eyak.prescription.service;

import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PrescriptionService {
    Prescription insert(PrescriptionDto prescriptionDto, Long memberId);
    List<Prescription> findAllByMemberId(Long memberId);
    List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime);
    Prescription findById(Long prescriptionId, Long memberId);
    Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId);
    void delete(Long prescriptionId, Long memberId);
    List<PrescriptionMedicineRoutine> findPrescriptionMedicineRoutinesById(Long prescriptionId, Long memberId);
}
