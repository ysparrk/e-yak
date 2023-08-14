package now.eyak.routine.repository;

import java.time.LocalDate;
import java.util.List;
import now.eyak.member.domain.Member;
import now.eyak.routine.dto.query.MedicineRoutineCheckDateQueryDto;

public interface CustomMedicineRoutineCheckRepository {
    List<MedicineRoutineCheckDateQueryDto> findByDateAndMember(LocalDate date, Member member);
}
