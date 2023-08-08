package now.eyak.routine.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.dto.request.MedicineRoutineCheckDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.dto.response.MedicineRoutineDateResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthDateDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthResponseDto;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import now.eyak.survey.service.ContentTextResultService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;

@EnableScheduling  // 스케줄링 활성화
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MedicineRoutineCheckServiceImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MedicineRoutineCheckService medicineRoutineCheckService;
    @Autowired
    ContentTextResultService contentTextResultService;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    PrescriptionService prescriptionService;
    @Autowired
    MedicineRoutineCheckRepository medicineRoutineCheckRepository;
    @Autowired
    MedicineRoutineRepository medicineRoutineRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;

    Member member;
    Prescription prescription;
    PrescriptionDto prescriptionDto;
    MedicineRoutineCheckDto medicineRoutineCheckDto;
    MedicineRoutineCheck medicineRoutineCheck;
    Survey survey;
    SurveyContent surveyContent;
    ContentTextResultDto contentTextResultDto;

    static List<Routine> routines;

    @BeforeEach
    void beforeEach() {

        member = Member.builder()
                .providerName("google")
                .nickname("박길동")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(2, 0))
                .build();
        member = memberRepository.save(member);

        // 약 1
        routines = List.of(Routine.BED_AFTER, Routine.LUNCH_AFTER, Routine.DINNER_AFTER);
        
        prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .iotLocation(4)
                .medicineDose(1)
                .unit("정")
                .routines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());


        routines = List.of(Routine.BED_BEFORE, Routine.LUNCH_AFTER, Routine.DINNER_BEFORE);

        // 약 2
        prescriptionDto = PrescriptionDto.builder()
                .customName("혈압약")
                .icd("RS-12345678")
                .krName("고혈압 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .iotLocation(3)
                .medicineDose(1)
                .unit("정")
                .routines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());

        medicineRoutineCheckService.scheduleMedicineRoutineCheck();


    }

    @DisplayName("Scheduling")
    @Test
    @Transactional
    void scheduleMedicineRoutineCheck() {
        // given

        // when

        // then
        List<Prescription> allByMemberIdBetweenDate = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now());
        Integer routinesCnt = allByMemberIdBetweenDate.stream().map(prescription1 -> prescription1.getPrescriptionMedicineRoutines().size()).reduce(0, (a, b) -> a + b);
        Assertions.assertThat(routinesCnt).isEqualTo(medicineRoutineCheckRepository.findByMemberAndDate(member, LocalDate.now()).size());

    }

    @DisplayName("Routine Check")
    @Test
    @Transactional
    void updateMedicineRoutineCheck() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(testPrescription.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // toggle

        // then
        Assertions.assertThat(true).isEqualTo(medicineRoutineCheckRepository.findByIdAndMemberAndDate(testPrescription.getId(), member, LocalDate.now())
                        .orElseThrow(() -> new NoSuchElementException("해당 복약에 대한 체크가 존재하지 않습니다."))
                        .getTook()
                );
    }

    @DisplayName("Date to Month")
    @Test
    @Transactional
    void getDateResultsByDateAndMember() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(testPrescription.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // true 값 하나 만들기

        MedicineRoutineMonthDateDto dateResults = medicineRoutineCheckService.getDateResultsByDateAndMember(LocalDate.now(), member.getId());

        // then
        // TODO: Assertion 작성

    }

    @DisplayName("Month")
    @Test
    @Transactional
    void getMonthResultsByMonthAndMember() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(testPrescription.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // true 값 하나 만들기
        MedicineRoutineMonthResponseDto monthResult = medicineRoutineCheckService.getMonthResultsByMonthAndMember(YearMonth.now(), member.getId()); // 데이터 모으기

        // then
        System.out.println("monthResult = " + monthResult);
        // TODO: Assertion 작성

    }

    @DisplayName("Date Detail")
    @Test
    @Transactional
    void getDateDetailResultsByDateAndMember() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(testPrescription.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId()); // true까지 표시


        survey = Survey.builder()
                .date(LocalDate.now())
                .build();

        survey = surveyRepository.save(survey);

        surveyContent = SurveyContent.builder()
                .survey(survey)
                .build();

        surveyContent = surveyContentRepository.save(surveyContent);

        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .surveyContentId(surveyContent.getId())
                .build();

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, member.getId());

        MedicineRoutineDateResponseDto detailResult = medicineRoutineCheckService.getDateDetailResultsByDateAndMember(LocalDate.now(), member.getId()); // 결과 불러오기

        // then
        Assertions.assertThat(contentTextResult.getText()).isEqualTo(detailResult.getSurveyContentDtos().get(0).getContentTextResultResponse().get(0).getText());
        System.out.println("detailResult = " + detailResult.getMedicineRoutineDateDtos());
        System.out.println("detailResult = " + detailResult);
        // TODO: Assertions 작성
    }

}