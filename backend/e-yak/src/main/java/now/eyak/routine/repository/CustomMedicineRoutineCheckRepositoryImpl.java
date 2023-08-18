package now.eyak.routine.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.routine.domain.QMedicineRoutineCheck;
import now.eyak.routine.dto.query.MedicineRoutineCheckDateQueryDto;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CustomMedicineRoutineCheckRepositoryImpl implements CustomMedicineRoutineCheckRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MedicineRoutineCheckDateQueryDto> findByDateAndMember(LocalDate date, Member member) {
        // 복용 정보
        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;


        return queryFactory
                .select(Projections.constructor(MedicineRoutineCheckDateQueryDto.class,
                        qMedicineRoutineCheck.prescription.id,
                        qMedicineRoutineCheck.medicineRoutine.routine,
                        qMedicineRoutineCheck.took))
                .from(qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.date.eq(date).and(qMedicineRoutineCheck.member.eq(member)))
                .fetch();
    }
}
