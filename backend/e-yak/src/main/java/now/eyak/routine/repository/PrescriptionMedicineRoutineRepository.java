package now.eyak.routine.repository;

import now.eyak.prescription.domain.Prescription;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionMedicineRoutineRepository extends JpaRepository<PrescriptionMedicineRoutine, Long> {
    List<PrescriptionMedicineRoutine> findByPrescription(Prescription prescription);
}
