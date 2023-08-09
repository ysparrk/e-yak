package now.eyak.prescription.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.domain.QPrescription;
import now.eyak.prescription.dto.MedicineRoutineUpdateDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.prescription.dto.query.*;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.domain.QMedicineRoutineCheck;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import now.eyak.routine.repository.PrescriptionMedicineRoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MemberRepository memberRepository;
    private final MedicineRoutineRepository medicineRoutineRepository;
    private final PrescriptionMedicineRoutineRepository prescriptionMedicineRoutineRepository;
    private final MedicineRoutineCheckRepository medicineRoutineCheckRepository;

    private final JPAQueryFactory queryFactory;

    /**
     * 복약 정보를 등록한다.
     * @param prescriptionDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription insert(PrescriptionDto prescriptionDto, Long memberId) {
        Prescription prescription = prescriptionDto.toEntity();
        log.debug("prescriptionDto = {}", prescriptionDto);
        Member member = getMemberOrThrow(memberId);
        prescription.setMember(member);

        prescriptionDto.getMedicineRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .medicineRoutine(medicineRoutine)
                    .prescription(prescription)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // 복약 등록하면 MedicineCheck 테이블 생성
        List<PrescriptionMedicineRoutine> allRoutines = prescriptionMedicineRoutineRepository.findByPrescription(prescription);

        LocalDateTime createdAt = savedPrescription.getCreatedAt();
        LocalTime createdTime = createdAt.toLocalTime(); // 등록 시간
        LocalTime eatingDuration = member.getEatingDuration();  // 식사 시간

        List<LocalTime> times = new ArrayList<>();
        times.add(member.getWakeTime());
        times.add(member.getBreakfastTime());
        times.add(member.getBreakfastTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getLunchTime());
        times.add(member.getLunchTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getDinnerTime());
        times.add(member.getDinnerTime().plusHours(eatingDuration.getHour()).plusMinutes(eatingDuration.getMinute()));
        times.add(member.getBedTime()); // TODO: BED_TIME 12시 이후 일 경우 고려해서 로직 작성
        times.add(createdTime);
        Collections.sort(times);

        int createdIdx = times.indexOf(createdTime);  // 등록 시간의 idx

        // routine 리스트
        List<Routine> routines = new ArrayList<>();
        routines.addAll(Arrays.stream(Routine.values()).toList());


        for (PrescriptionMedicineRoutine prescriptionMedicineRoutine : allRoutines) {
            if (routines.indexOf(prescriptionMedicineRoutine.getMedicineRoutine().getRoutine()) < createdIdx) continue;

            MedicineRoutineCheck medicineRoutineCheck = MedicineRoutineCheck.builder()
                    .date(LocalDate.now())
                    .medicineRoutine(prescriptionMedicineRoutine.getMedicineRoutine())
                    .prescription(prescription)
                    .took(false) // 초기 값 false
                    .member(member)
                    .build();
            medicineRoutineCheckRepository.save(medicineRoutineCheck);

        }

        return savedPrescription;
    }

    @Transactional
    @Override
    public List<Prescription> findAllByMemberId(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return prescriptionRepository.findAllByMember(member);
    }

    /**
     * 사용자(memberId)의 복약 정보 중 dateTime에 복용해야하는 복약 정보를 반환한다.
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public List<Prescription> findAllByMemberIdBetweenDate(Long memberId, LocalDateTime dateTime) {
        Member member = getMemberOrThrow(memberId);

        return prescriptionRepository.findAllByMemberAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(member, dateTime, dateTime);
    }


    /**
     * 사용자(memberId)의 복약 정보 중 dateTime에 복용해야하는 복약 정보를 반환한다.
     * 루틴에 대해 리스트업 해서 전달하기
     * @param memberId
     * @param dateTime
     * @return
     */
    @Override
    public PrescriptionResponseDto findAllAndSortWithRoutine(Long memberId, LocalDateTime dateTime) {
        /**
         * 1. 요청받은 member
         * 2. 요청받은 date에 대해서 startDate ~ endDate
         * 3. 요청받은 date와 startDate가 같다면, 루틴에 대한 필터링이 필요
         * 4. 루틴별로 처방전 id 넣기
         */

        Member member = getMemberOrThrow(memberId);

        QPrescription qPrescription = QPrescription.prescription;
        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;

        // BedAfter
        List<BedAfterQueryDto> bedAfterQueryDtoList = queryFactory
                .select(Projections.constructor(BedAfterQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                        ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.BED_AFTER)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // BreakfastBefore
        List<BreakfastBeforeQueryDto> breakfastBeforeQueryDtoList = queryFactory
                .select(Projections.constructor(BreakfastBeforeQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.BREAKFAST_BEFORE)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // BreakfastAfter
        List<BreakfastAfterQueryDto> breakfastAfterQueryDtoList = queryFactory
                .select(Projections.constructor(BreakfastAfterQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.BREAKFAST_AFTER)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // LunchBefore
        List<LunchBeforeQueryDto> lunchBeforeQueryDtoList = queryFactory
                .select(Projections.constructor(LunchBeforeQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.LUNCH_BEFORE)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // LunchAfter
        List<LunchAfterQueryDto> lunchAfterQueryDtoList = queryFactory
                .select(Projections.constructor(LunchAfterQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.LUNCH_AFTER)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // DinnerBefore
        List<DinnerBeforeQueryDto> dinnerBeforeQueryDtoList = queryFactory
                .select(Projections.constructor(DinnerBeforeQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.DINNER_BEFORE)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // DinnerAfter
        List<DinnerAfterQueryDto> dinnerAfterQueryDtoList = queryFactory
                .select(Projections.constructor(DinnerAfterQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.DINNER_AFTER)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();

        // BedBefore
        List<BedBeforeQueryDto> bedBeforeQueryDtoList = queryFactory
                .select(Projections.constructor(BedBeforeQueryDto.class,
                        qPrescription.id,
                        qPrescription.customName,
                        qPrescription.iotLocation,
                        qPrescription.medicineShape
                ))
                .from(qPrescription, qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.medicineRoutine.routine.eq(Routine.BED_BEFORE)
                        .and(qMedicineRoutineCheck.prescription.id.eq(qPrescription.id))
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.date.eq(dateTime.toLocalDate()))
                        .and(qPrescription.member.eq(member))
                        .and(qPrescription.startDateTime.loe(dateTime))
                        .and(qPrescription.endDateTime.gt(dateTime.toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1)))
                )
                .fetch();


        // responseDto에 리스트 넣기
        PrescriptionResponseDto prescriptionResponseDto = PrescriptionResponseDto.builder()
                .bedAfterQueryResponses(bedAfterQueryDtoList)
                .breakfastBeforeQueryResponses(breakfastBeforeQueryDtoList)
                .breakfastAfterQueryResponses(breakfastAfterQueryDtoList)
                .lunchBeforeQueryResponses(lunchBeforeQueryDtoList)
                .lunchAfterQueryResponses(lunchAfterQueryDtoList)
                .dinnerBeforeQueryResponses(dinnerBeforeQueryDtoList)
                .dinnerAfterQueryResponses(dinnerAfterQueryDtoList)
                .bedBeforeQueryResponses(bedBeforeQueryDtoList)
                .build();

        return prescriptionResponseDto;

    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 반환한다.
     *
     * @param prescriptionId
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription findById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 수정한다.
     *
     * @param prescriptionId
     * @param prescriptionDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionAndCheckPermission(prescriptionId, member);

        prescriptionDto.update(prescription);

        prescription.getPrescriptionMedicineRoutines().clear();
        prescriptionDto.getMedicineRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .prescription(prescription)
                    .medicineRoutine(medicineRoutine)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        return prescriptionRepository.save(prescription);
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)를 삭제한다.
     *
     * @param prescriptionId
     * @param memberId
     */
    @Transactional
    @Override
    public void delete(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        prescriptionRepository.deleteByIdAndMember(prescriptionId, member);
    }

    /**
     * 사용자(memberId)의 복약 정보(prescriptionId)의 복약 루틴을 반환한다.
     *
     * @param prescriptionId
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<PrescriptionMedicineRoutine> findPrescriptionMedicineRoutinesById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescriptionMedicineRoutineRepository.findByPrescription(prescription);
    }

    @Transactional
    @Override
    public List<PrescriptionMedicineRoutine> updatePrescriptionMedicineRoutinesById(MedicineRoutineUpdateDto medicineRoutineUpdateDto, Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        prescription.getPrescriptionMedicineRoutines().clear();
        medicineRoutineUpdateDto.getRoutines().stream().forEach(routine -> {
            MedicineRoutine medicineRoutine = medicineRoutineRepository.findByRoutine(routine).orElseThrow(() -> new NoSuchElementException("해당하는 Routine이 존재하지 않습니다."));
            PrescriptionMedicineRoutine prescriptionMedicineRoutine = PrescriptionMedicineRoutine.builder()
                    .prescription(prescription)
                    .medicineRoutine(medicineRoutine)
                    .build();

            prescription.add(prescriptionMedicineRoutine);
        });

        return prescriptionRepository.save(prescription).getPrescriptionMedicineRoutines();
    }

    private Prescription getPrescriptionAndCheckPermission(Long prescriptionId, Member member) {
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    private Prescription getPrescriptionByIdAndMemberOrThrow(Long prescriptionId, Member member) {
        Prescription prescription = prescriptionRepository.findByIdAndMember(prescriptionId, member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당 Prescription은 존재하지 않습니다."));
        return prescription;
    }

    private Member getMemberOrThrow(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        return member;
    }


}
