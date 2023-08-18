package now.eyak.routine.repository;

import now.eyak.member.domain.Member;
import now.eyak.routine.dto.query.MedicineRoutineCheckDateQueryDto;

import java.time.LocalDate;
import java.util.List;

public interface CustomMedicineRoutineCheckRepository {
    List<MedicineRoutineCheckDateQueryDto> findByDateAndMember(LocalDate date, Member member);
}
