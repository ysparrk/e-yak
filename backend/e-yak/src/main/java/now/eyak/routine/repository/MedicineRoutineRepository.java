package now.eyak.routine.repository;

import now.eyak.routine.domain.MedicineRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRoutineRepository extends JpaRepository<MedicineRoutine, Long> {
}
