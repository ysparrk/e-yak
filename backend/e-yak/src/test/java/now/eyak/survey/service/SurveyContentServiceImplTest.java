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

        survey = Survey.builder()
                .date(LocalDate.of(2023,8,01))
                .build();

        survey = surveyRepository.save(survey);

        surveyContent = SurveyContent.builder()
                .survey(survey)
                .build();

        surveyContent = surveyContentRepository.save(surveyContent);

        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.SOSO)
                .surveyContentId(surveyContent.getId())
                .build();

        contentStatusResultDto = ContentStatusResultDto.builder()
                .selectedStatusChoices(Arrays.asList(ChoiceStatus.HEADACHE, ChoiceStatus.NAUSEA, ChoiceStatus.FEVER))
                .surveyContentId(surveyContent.getId())
                .build();

        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .surveyContentId(surveyContent.getId())
                .build();

    }
    

    @DisplayName("ALL GET")
    @Test
    @Transactional
    void getSurveyResultByDateAndMember() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, member.getId());
        ContentStatusResult savedContentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, member.getId());
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, member.getId());

        // when
        List<SurveyContentDto> surveyResultByDateAndMember = surveyContentService.getSurveyResultByDateAndMember(survey.getDate(), member.getId());

        // then
        System.out.println("surveyResultByDateAndMember = " + surveyResultByDateAndMember);
        Assertions.assertThat(savedContentEmotionResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.get(0).getContentEmotionResultResponses().get(0).getMemberId());
        Assertions.assertThat(savedContentStatusResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.get(0).getContentStatusResultResponses().get(0).getMemberId());
        Assertions.assertThat(savedContentTextResult.getMember().getId()).isEqualTo(surveyResultByDateAndMember.get(0).getContentTextResultResponse().get(0).getMemberId());

    }
}