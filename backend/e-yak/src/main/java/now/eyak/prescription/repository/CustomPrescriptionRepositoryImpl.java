package now.eyak.prescription.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.domain.QPrescription;
import now.eyak.prescription.dto.query.PrescriptionListQueryDto;
import now.eyak.prescription.dto.query.PrescriptionRoutineFutureQueryDto;
import now.eyak.prescription.dto.query.PrescriptionRoutineQueryDto;
import now.eyak.routine.domain.QMedicineRoutineCheck;
import now.eyak.routine.dto.query.MedicineRoutineCheckBetweenDatesQueryDto;
import now.eyak.routine.enumeration.Routine;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static now.eyak.prescription.domain.QPrescription.prescription;
import static now.eyak.routine.domain.QMedicineRoutineCheck.medicineRoutineCheck;
import static now.eyak.routine.domain.QPrescriptionMedicineRoutine.prescriptionMedicineRoutine;

@RequiredArgsConstructor
public class CustomPrescriptionRepositoryImpl implements CustomPrescriptionRepository{

    private final JPAQueryFactory queryFactory;

    /**
     * 요청받은 date에 복용하는 약 확인
     * @param member
     * @param dateTime
     * @return
     */
    @Override
    public List<Prescription> findAllByMemberAndBetweenStartAndEndDateTime(Member member, LocalDateTime dateTime) {

        QPrescription qPrescription = QPrescription.prescription;

        List<Prescription> prescriptionList = queryFactory
                .select(qPrescription)
                .from(qPrescription)
                .join(qPrescription.prescriptionMedicineRoutines, prescriptionMedicineRoutine)
                .fetchJoin()
                .where(qPrescription.member.eq(member)
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.goe(dateTime.toLocalDate().atStartOfDay().minusDays(1))))
                .fetch();

        return prescriptionList;
    }

    /**
     * 요청받은 날짜 사이에 복용중인 약 리스트 반환
     * @param member
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    @Transactional
    @Override
    public List<PrescriptionListQueryDto> findAllByMemberAndBetweenDates(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        QPrescription qPrescription = QPrescription.prescription;
        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;


        // 그 기간에 복용하는 약 조회
        List<Prescription> prescriptionList = queryFactory
                .select(qPrescription)
                .from(qPrescription)
                .join(qPrescription.prescriptionMedicineRoutines, prescriptionMedicineRoutine)
                .fetchJoin()
                .where(qPrescription.member.eq(member)
                        .and(qPrescription.startDateTime.loe(endDateTime.toLocalDate().atStartOfDay()))
                        .and(qPrescription.endDateTime.goe(startDateTime.toLocalDate().atStartOfDay())))
                .orderBy(qPrescription.startDateTime.asc())
                .fetch();


        List<PrescriptionListQueryDto> prescriptionResultList = new ArrayList<>();

        // 각 처방전 별로 fullDose, actualDose 계산
        for (Prescription prescription : prescriptionList) {

            List<MedicineRoutineCheckBetweenDatesQueryDto> dateResults = queryFactory
                    .select(Projections.constructor(MedicineRoutineCheckBetweenDatesQueryDto.class,
                            qMedicineRoutineCheck.took))
                    .from(qMedicineRoutineCheck)
                    .where(qMedicineRoutineCheck.member.eq(member)
                            .and(qMedicineRoutineCheck.prescription.eq(prescription))
                            .and(qMedicineRoutineCheck.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())))
                    .fetch();


            Long fullDose = (long) dateResults.size();

            // 실제 복용량 계산
            Long actualDose = dateResults.stream()
                    .filter(MedicineRoutineCheckBetweenDatesQueryDto::isTook)
                    .count();

            // builder
            PrescriptionListQueryDto prescriptionListQueryDto = PrescriptionListQueryDto.builder()
                    .customName(prescription.getCustomName())
                    .icd(prescription.getIcd())
                    .krName(prescription.getKrName())
                    .engName(prescription.getEngName())
                    .startDateTime(prescription.getStartDateTime())
                    .endDateTime(prescription.getEndDateTime())
                    .iotLocation(prescription.getIotLocation())
                    .medicineShape(prescription.getMedicineShape())
                    .medicineDose(prescription.getMedicineDose())
                    .unit(prescription.getUnit())
                    .medicineRoutines(prescription.getPrescriptionMedicineRoutines().stream().map(prescriptionRoutine -> prescriptionRoutine.getMedicineRoutine().getRoutine()).toList())
                    .fullDose(fullDose)
                    .actualDose(actualDose)
                    .build();

            prescriptionResultList.add(prescriptionListQueryDto);

        }

        return prescriptionResultList;
    }

    /**
     * 요청받은 member, routine, date에 복용하는 약 리스트 반환(현재)
     * @param routine
     * @param member
     * @param dateTime
     * @return
     */
    @Transactional
    @Override
    public List<PrescriptionRoutineQueryDto> findByRoutine(Routine routine, Member member, LocalDateTime dateTime) {

        List<PrescriptionRoutineQueryDto> routineQueryList = queryFactory
                .select(Projections.constructor(PrescriptionRoutineQueryDto.class,
                        prescription.id,
                        prescription.customName,
                        prescription.iotLocation,
                        prescription.medicineShape,
                        medicineRoutineCheck.took
                ))
                .from(prescription, medicineRoutineCheck)
                .where(medicineRoutineCheck.medicineRoutine.routine.eq(routine)
                        .and(medicineRoutineCheck.prescription.id.eq(prescription.id))
                        .and(medicineRoutineCheck.member.eq(member))
                        .and(medicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(prescription.member.eq(member))
                        .and(prescription.startDateTime.loe(dateTime.toLocalDate().atStartOfDay()))
                        .and(prescription.endDateTime.goe(dateTime.toLocalDate().atStartOfDay()))
                )
                .fetch();

        return routineQueryList;
    }

    /**
     * 요청받은 member, routine, date에 복용하는 약 리스트 반환(미래)
     * @param routine
     * @param member
     * @param dateTime
     * @return
     */
    @Transactional
    @Override
    public List<PrescriptionRoutineFutureQueryDto> findByRoutineForFuture(Routine routine, Member member, LocalDateTime dateTime) {

        List<PrescriptionRoutineFutureQueryDto> routineQueryList = queryFactory
                .selectDistinct(Projections.constructor(PrescriptionRoutineFutureQueryDto.class,
                        prescription.id,
                        prescription.customName,
                        prescription.iotLocation,
                        prescription.medicineShape
                ))
                .from(prescription, medicineRoutineCheck)
                .where(prescription.prescriptionMedicineRoutines.any().medicineRoutine.routine.eq(routine)
                        .and(prescription.member.eq(member))
                        .and(prescription.startDateTime.loe(dateTime.toLocalDate().atStartOfDay()))
                        .and((prescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay())
                                .or(prescription.endDateTime.eq(dateTime.toLocalDate().atStartOfDay())
                                        .and(prescription.id.notIn(
                                                JPAExpressions.select(medicineRoutineCheck.prescription.id)
                                                        .from(medicineRoutineCheck)
                                                        .where((medicineRoutineCheck.prescription.startDateTime.eq(prescription.startDateTime))
                                                        .and(medicineRoutineCheck.medicineRoutine.routine.eq(routine))
                                                        .and(medicineRoutineCheck.member.eq(member))
                                                    )))))))
                .fetch();

        return routineQueryList;
    }

    @Override
    public Optional<Prescription> findByIdAndMember(Long id, Member member) {
        Prescription findPrescription = queryFactory
                .select(prescription)
                .from(prescription)
                .join(prescription.prescriptionMedicineRoutines, prescriptionMedicineRoutine)
                .where(prescription.id.eq(id).and(prescription.member.id.eq(member.getId())))
                .fetchJoin()
                .fetchOne();

        return Optional.ofNullable(findPrescription);
    }

    @Override
    public List<Prescription> findAllByMember(Member member) {
        List<Prescription> prescriptions = queryFactory
                .select(prescription)
                .from(prescription)
                .join(prescription.prescriptionMedicineRoutines, prescriptionMedicineRoutine)
                .where(prescription.member.id.eq(member.getId()))
                .fetchJoin()
                .fetch();

        return prescriptions;
    }
}
