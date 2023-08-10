package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.*;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.enumeration.ChoiceStatus;
import now.eyak.survey.enumeration.SurveyContentType;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SurveyContentServiceImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyContentService surveyContentService;
    @Autowired
    ContentTextResultService contentTextResultService;
    @Autowired
    ContentStatusResultService contentStatusResultService;
    @Autowired
    ContentEmotionResultService contentEmotionResultService;


    Member member;
    Survey survey;
    SurveyContent surveyContent;
    ContentTextResultDto contentTextResultDto;
    ContentStatusResultDto contentStatusResultDto;
    ContentEmotionResultDto contentEmotionResultDto;


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

        survey = surveyRepository.findByDate(LocalDate.now()).orElseThrow(() -> new NoSuchElementException("해당 날짜에 설문이 존재하지 않습니다."));

        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.SOSO)
                .build();

        contentStatusResultDto = ContentStatusResultDto.builder()
                .selectedStatusChoices(Arrays.asList(ChoiceStatus.HEADACHE, ChoiceStatus.NAUSEA, ChoiceStatus.FEVER))
                .build();

        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();

    }
    

    @DisplayName("ALL GET")
    @Test
    @Transactional
    void getSurveyResultByDateAndMember() {
        // given
        List<SurveyContent> surveyContents = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now());
        SurveyContent surveyContentChoiceEmotion = surveyContents.stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.CHOICE_EMOTION)).findAny().get();
        SurveyContent surveyContentChoiceStatus = surveyContents.stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.CHOICE_STATUS)).findAny().get();
        SurveyContent surveyContentChoiceText = surveyContents.stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().get();


        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContentChoiceEmotion.getId(), member.getId());
        ContentStatusResult savedContentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, surveyContentChoiceStatus.getId(), member.getId());
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContentChoiceText.getId(), member.getId());

        // when
        SurveyContentDto surveyResultByDateAndMember = surveyContentService.getSurveyResultByDateAndMember(survey.getDate(), member.getId());

        // then
        System.out.println("surveyResultByDateAndMember = " + surveyResultByDateAndMember);
        Assertions.assertThat(savedContentEmotionResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.getContentEmotionResultResponse().getMemberId());
        Assertions.assertThat(savedContentStatusResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.getContentStatusResultResponse().getMemberId());
        Assertions.assertThat(savedContentTextResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.getContentTextResultResponse().getMemberId());

    }

    @DisplayName("매일 Survey와 SurveyContent를 삽입하는 스케줄링 메서드 테스트")
    @Test
    @Transactional
    void insertSurveyAndSurveyContentPerDay() {
        //given

        //when
//        surveyContentService.insertSurveyAndSurveyContentPerDay();
//
//        //then
//        survey
    }
}