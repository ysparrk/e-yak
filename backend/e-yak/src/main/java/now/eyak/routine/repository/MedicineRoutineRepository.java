package now.eyak.routine.repository;

import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.routine.enumeration.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRoutineRepository extends JpaRepository<MedicineRoutine, Long> {
    Optional<MedicineRoutine> findByRoutine(Routine routine);
}
