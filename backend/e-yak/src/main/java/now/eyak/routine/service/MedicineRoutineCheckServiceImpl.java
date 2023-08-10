package now.eyak.routine.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.domain.QMedicineRoutineCheck;
import now.eyak.routine.dto.query.MedicineRoutineCheckDateQueryDto;
import now.eyak.routine.dto.query.MedicineRoutineCheckMonthQueryDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckIdDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.dto.response.*;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.PrescriptionMedicineRoutineRepository;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.service.SurveyContentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineRoutineCheckServiceImpl implements MedicineRoutineCheckService {

    private final MemberRepository memberRepository;
    private final MedicineRoutineCheckRepository medicineRoutineCheckRepository;
    private final PrescriptionMedicineRoutineRepository prescriptionMedicineRoutineRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionService prescriptionService;
    private final SurveyContentService surveyContentService;

    private final JPAQueryFactory queryFactory;

    /**
     * 스케줄링 적용 0시 0분 0초마다
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
    public void scheduleMedicineRoutineCheck() {

        //TODO: batch 처리, 당일 등록한 약에 대한 row 생성
        List<Member> allMembers = memberRepository.findAll();

        // 각 사용자 정보에 대해 필요한 작업을 처리
        for (Member member : allMembers) {
            // memberId로 Prescription 정보 조회 -> 지난 날짜는 필요 없음. 해당 날짜에만
            Long memberId = member.getId();

            List<Prescription> allPrescriptions = prescriptionService.findAllByMemberIdBetweenDate(memberId, LocalDateTime.now());

            for (Prescription prescription : allPrescriptions) {

                List<PrescriptionMedicineRoutine> allRoutines = prescriptionMedicineRoutineRepository.findByPrescription(prescription);

                ZonedDateTime createdAt = prescription.getCreatedAt();
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
                    if (routines.indexOf(prescriptionMedicineRoutine.getMedicineRoutine().getRoutine()) >= createdIdx) continue;

                    MedicineRoutineCheck medicineRoutineCheck = MedicineRoutineCheck.builder()
                            .date(LocalDate.now())
                            .medicineRoutine(prescriptionMedicineRoutine.getMedicineRoutine())
                            .prescription(prescription)
                            .took(false) // 초기 값 false
                            .member(member)
                            .build();

                    medicineRoutineCheckRepository.save(medicineRoutineCheck);
                }
            }
        }

    }

    /**
     * 약 복용 체크 기록 및 수정(true or false)
     * @param medicineRoutineCheckUpdateDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public MedicineRoutineCheck updateMedicineRoutineCheck(MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        MedicineRoutineCheck medicineRoutineCheck = medicineRoutineCheckRepository.findByIdAndMemberAndDate(medicineRoutineCheckUpdateDto.getId(), member, medicineRoutineCheckUpdateDto.getDate())
                .orElseThrow(() -> new NoSuchElementException("해당 날짜에 회원에 대한 복약 기록이 존재하지 않습니다."));

        medicineRoutineCheckUpdateDto.update(medicineRoutineCheck);  // toggle

        return medicineRoutineCheckRepository.save(medicineRoutineCheck);
    }


    /**
     * 일자에 따른 약 복용량 반환 -> Day to Month
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public MedicineRoutineMonthDateDto getDateResultsByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        // 요청받은 날짜의 복약 기록을 다 가져오기
        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;

        List<MedicineRoutineCheckMonthQueryDto> dateResults = queryFactory
                .select(Projections.constructor(MedicineRoutineCheckMonthQueryDto.class,
                        qMedicineRoutineCheck.date,
                        qMedicineRoutineCheck.took))
                .from(qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.date.eq(date).and(qMedicineRoutineCheck.member.eq(member)))
                .fetch();

        Long fullDose = (long) dateResults.size();

        // 실제 복용 개수 계산
        Long actualDose = dateResults.stream()
                .filter(MedicineRoutineCheckMonthQueryDto::isTook)
                .count();

        MedicineRoutineMonthDateDto medicineRoutineMonthDateDto = MedicineRoutineMonthDateDto.builder()
                .date(date)
                .fullDose(fullDose)
                .actualDose(actualDose)
                .build();

        return medicineRoutineMonthDateDto;
    }

    /**
     * 한달 단위 복용 확인(Month)
     * @param yearMonth
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<MedicineRoutineMonthDateDto> getMonthResultsByMonthAndMember(YearMonth yearMonth, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<MedicineRoutineMonthDateDto> datesResponseList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<MedicineRoutineMonthDateDto> dateResponse= Collections.singletonList(getDateResultsByDateAndMember(date, memberId));
            datesResponseList.addAll(dateResponse);
        }

        return datesResponseList;

    }

    /**
     * 하루 단위 복용 상세 조회(약 하나에 따른 복용 내용 + 설문조사)
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public MedicineRoutineDateResponseDto getDateDetailResultsByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        // survey
        SurveyContentDto surveyResults = surveyContentService.getSurveyResultByDateAndMember(date, memberId);


        // 복용 정보
        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;


        List<MedicineRoutineCheckDateQueryDto> dateResults = queryFactory
                .select(Projections.constructor(MedicineRoutineCheckDateQueryDto.class,
                        qMedicineRoutineCheck.prescription.id,
                        qMedicineRoutineCheck.medicineRoutine.routine,
                        qMedicineRoutineCheck.took))
                .from(qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.date.eq(date).and(qMedicineRoutineCheck.member.eq(member)))
                .fetch();


        List<MedicineRoutineDateDto> medicineDetails = dateResults.stream()
                .map(result -> {
                    Prescription findPrescription = prescriptionRepository.findById(result.getPrescriptionId()).orElseThrow(() -> new NoSuchElementException("해당하는 복약 정보의 id가 없습니다."));

                    return MedicineRoutineDateDto.builder()
                            .prescriptionId(result.getPrescriptionId())
                            .customName(findPrescription.getCustomName())
                            .routine(result.getRoutine())
                            .took(result.getTook())
                            .build();
                })
                .collect(Collectors.toList());

        MedicineRoutineDateResponseDto medicineRoutineDateResponseDto = MedicineRoutineDateResponseDto.builder()
                .date(date)
                .medicineRoutineDateDtos(medicineDetails)
                .surveyContentDtos(surveyResults)
                .build();

        return medicineRoutineDateResponseDto;

    }

    /**
     * MedicineRoutineCheck의 id 조회(요청한 date, member, routine, prescription으로 조회)
     * @param medicineRoutineCheckIdDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public MedicineRoutineCheckIdResponseDto getMedicineRoutineCheckId(MedicineRoutineCheckIdDto medicineRoutineCheckIdDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        QMedicineRoutineCheck qMedicineRoutineCheck = QMedicineRoutineCheck.medicineRoutineCheck;

        MedicineRoutineCheckIdResponseDto getId = queryFactory
                .select(Projections.constructor(MedicineRoutineCheckIdResponseDto.class,
                        qMedicineRoutineCheck.id,
                        qMedicineRoutineCheck.took
                        ))
                .from(qMedicineRoutineCheck)
                .where(qMedicineRoutineCheck.date.eq(medicineRoutineCheckIdDto.getDate())
                        .and(qMedicineRoutineCheck.member.eq(member))
                        .and(qMedicineRoutineCheck.prescription.id.eq(medicineRoutineCheckIdDto.getPrescriptionId()))
                        .and(qMedicineRoutineCheck.medicineRoutine.routine.eq(medicineRoutineCheckIdDto.getRoutine())))
                .fetchOne();

        return getId;
    }

}
