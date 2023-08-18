package now.eyak.routine.repository;

import now.eyak.member.domain.Member;
import now.eyak.routine.domain.MedicineRoutineCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MedicineRoutineCheckRepository extends JpaRepository<MedicineRoutineCheck, Long>, CustomMedicineRoutineCheckRepository {
    Optional<MedicineRoutineCheck> findByIdAndMemberAndDate(Long id, Member member, LocalDate date);
}
