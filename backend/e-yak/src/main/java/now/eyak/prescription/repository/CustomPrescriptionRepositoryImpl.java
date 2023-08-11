package now.eyak.prescription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.domain.QMember;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.domain.QPrescription;

import java.time.LocalDateTime;
import java.util.List;

import static now.eyak.routine.domain.QPrescriptionMedicineRoutine.prescriptionMedicineRoutine;

@RequiredArgsConstructor
public class CustomPrescriptionRepositoryImpl implements CustomPrescriptionRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Prescription> findAllByMemberAndBetweenStartAndEndDateTime(Member member, LocalDateTime dateTime) {

        QMember qMember = QMember.member;
        QPrescription qPrescription = QPrescription.prescription;

        List<Prescription> prescriptionList = queryFactory
                .select(qPrescription)
                .from(qPrescription)
                .join(qPrescription.prescriptionMedicineRoutines, prescriptionMedicineRoutine)
                .fetchJoin()
                .where(qPrescription.member.eq(member)
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().minusDays(1))))
                .fetch();

        return prescriptionList;
    }
}
