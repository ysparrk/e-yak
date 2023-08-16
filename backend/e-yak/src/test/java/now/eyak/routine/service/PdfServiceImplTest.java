package now.eyak.routine.service;

import now.eyak.member.domain.Member;
import now.eyak.member.domain.enumeration.Role;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.routine.dto.response.PdfResponseDto;
import now.eyak.routine.enumeration.Routine;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.enumeration.ChoiceStatus;
import now.eyak.survey.enumeration.SurveyContentType;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import now.eyak.survey.service.ContentEmotionResultService;
import now.eyak.survey.service.ContentStatusResultService;
import now.eyak.survey.service.ContentTextResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
@SpringBootTest
class PdfServiceImplTest {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PrescriptionService prescriptionService;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    MedicineRoutineCheckService medicineRoutineCheckService;
    @Autowired
    PdfService pdfService;
    @Autowired
    ContentTextResultService contentTextResultService;
    @Autowired
    ContentStatusResultService contentStatusResultService;
    @Autowired
    ContentEmotionResultService contentEmotionResultService;

    static Member member;
    static List<Routine> routines;
    Survey survey;
    SurveyContent surveyContent;
    ContentEmotionResultDto contentEmotionResultDto;
    ContentTextResultDto contentTextResultDto;
    ContentStatusResultDto contentStatusResultDto;

    @BeforeEach
    void beforeEach() {
        // 회원 등록
        member = Member.builder()
            .providerId("google")
            .providerId("google_something")
            .refreshToken("refreshToken")
            .nickname("박길동")
            .role(Role.USER)
            .wakeTime(LocalTime.of(7, 0))
            .breakfastTime(LocalTime.of(8, 0))
            .lunchTime(LocalTime.of(14, 0))
            .dinnerTime(LocalTime.of(18, 0))
            .bedTime(LocalTime.of(23, 30))
            .eatingDuration(LocalTime.of(0, 30))
            .build();

        member = memberRepository.save(member);

        // 약 1
        routines = List.of(Routine.BED_AFTER, Routine.LUNCH_AFTER, Routine.DINNER_AFTER);

        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().toLocalDate().atStartOfDay())
                .endDateTime(LocalDateTime.now().plusDays(5))
                .iotLocation(4)
                .medicineDose(1.5f)
                .medicineShape(2)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        // 약 2
        routines = List.of(Routine.BED_AFTER, Routine.BED_BEFORE, Routine.DINNER_AFTER);

        PrescriptionDto prescriptionDto2 = PrescriptionDto.builder()
                .customName("혈압약")
                .icd("RS-1203123")
                .krName("혈압약")
                .engName("some english")
                .startDateTime(LocalDateTime.now().toLocalDate().atStartOfDay())
                .endDateTime(LocalDateTime.now().plusDays(10))
                .iotLocation(4)
                .medicineDose(1.5f)
                .medicineShape(2)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        Prescription inserted = prescriptionService.insert(prescriptionDto, member.getId());
        Prescription inserted2 = prescriptionService.insert(prescriptionDto2, member.getId());

        // survey
        survey = surveyRepository.findByDate(LocalDate.now()).orElseThrow(() -> new NoSuchElementException("해당 날짜에 설문이 존재하지 않습니다."));
        SurveyContent surveyContentStatus = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.CHOICE_STATUS)).findAny().get();
        SurveyContent surveyContentEmotion = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.CHOICE_EMOTION)).findAny().get();
        SurveyContent surveyContentText = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().orElseThrow(() -> new NoSuchElementException("해당하는 날짜에 TEXT 설문 문항이 존재하지 않습니다."));

        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.SOSO)
                .build();

        contentStatusResultDto = ContentStatusResultDto.builder()
                .selectedStatusChoices(Arrays.asList(ChoiceStatus.HEADACHE, ChoiceStatus.NAUSEA, ChoiceStatus.FEVER))
                .build();

        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();

//        contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContentEmotion.getId(), member.getId());
        contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, surveyContentStatus.getId(), member.getId());
        contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContentText.getId(), member.getId());
    }


    @DisplayName("Get PDF")
    @Test
    @Transactional
    void getPdfResponseByDatesAndMember() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(2);
        LocalDateTime endDateTime = LocalDateTime.now().toLocalDate().atStartOfDay();


        // when
        PdfResponseDto pdfResponse = pdfService.getPdfResponseByDatesAndMember(member.getId(), startDateTime, endDateTime);

        // then
        // TODO: Assertions 작성
        System.out.println("pdfResponse = " + pdfResponse);


    }
}