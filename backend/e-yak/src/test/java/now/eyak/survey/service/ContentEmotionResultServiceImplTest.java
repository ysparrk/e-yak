package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.repository.ContentEmotionResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContentEmotionResultServiceImplTest {

    @Autowired
    ContentEmotionResultService contentEmotionResultService;
    @Autowired
    ContentEmotionResultRepository contentEmotionResultRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    SurveyRepository surveyRepository;

    Member member;
    Survey survey;
    SurveyContent surveyContent;
    ContentEmotionResultDto contentEmotionResultDto;
    ContentTextResultResponseDto contentTextResultResponseDto;

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

    }


    @Test
    @Transactional
    @Rollback(false)
    void saveEmotionSurveyResult() {
        // given

        // when
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, member.getId());
        ContentEmotionResult findContentEmotionResult = contentEmotionResultRepository.findById(savedContentEmotionResult.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentEmotionResult가 없습니다."));

        // then
        System.out.println("savedContentEmotionResult = " + savedContentEmotionResult.getChoiceEmotion());
        System.out.println("findContentEmotionResult = " + findContentEmotionResult.getChoiceEmotion());
        Assertions.assertThat(savedContentEmotionResult).isEqualTo(findContentEmotionResult);
    }

    @Test
    @Transactional
    @Rollback(false)
    void updateEmotionSurveyResult() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, member.getId()); // 원본 값 저장
        System.out.println("savedContentEmotionResult.getChoiceEmotion() = " + savedContentEmotionResult.getChoiceEmotion());

        ContentEmotionResultDto contentEmotionResultDto = ContentEmotionResultDto.builder()
                .id(savedContentEmotionResult.getId())
                .choiceEmotion(ChoiceEmotion.GOOD)  // emotion 수정
                .surveyContentId(surveyContent.getId())
                .build();

        // when
        contentEmotionResultService.updateEmotionSurveyResult(contentEmotionResultDto, member.getId());
        ContentEmotionResult findContentEmotionResult = contentEmotionResultRepository.findById(contentEmotionResultDto.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentEmotionResult가 존재하지 않습니다."));

        // then
        Assertions.assertThat(findContentEmotionResult.getChoiceEmotion()).isEqualTo(contentEmotionResultDto.getChoiceEmotion());
        System.out.println("findContentEmotionResult.getChoiceEmotion() = " + findContentEmotionResult.getChoiceEmotion());
    }


    @Test
    @Transactional
    @Rollback(false)
    void deleteEmotionSurveyResult() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, member.getId());// 원본 값 저장
        System.out.println("savedContentEmotionResult.getChoiceEmotion() = " + savedContentEmotionResult.getChoiceEmotion());

        // when
        contentEmotionResultService.deleteEmotionSurveyResult(savedContentEmotionResult.getId(), member.getId());

        // then
        Assertions.assertThat(contentEmotionResultRepository.findById(savedContentEmotionResult.getId())).isEmpty();
        System.out.println("contentEmotionResultRepository = " + contentEmotionResultRepository.findById(savedContentEmotionResult.getId()));
    }

    @DisplayName("Emotion GET")
    @Test
    @Transactional
    @Rollback(false)
    void getEmotionResultsByDateAndMember() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, member.getId());// 원본 값 저장

        // 새로운 사용자 및 설문 응답 추가
        Member memberA = Member.builder()
                .providerName("google")
                .nickname("드민")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(2, 0))
                .build();
        memberA = memberRepository.save(memberA);


        Survey surveyA = Survey.builder()
                .date(LocalDate.of(2023,7,01))
                .build();

        surveyA = surveyRepository.save(surveyA);

        SurveyContent surveyContentA = SurveyContent.builder()
                .survey(surveyA)
                .build();

        surveyContentA = surveyContentRepository.save(surveyContentA);


        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.GOOD)
                .surveyContentId(surveyContentA.getId())
                .build();


        savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, memberA.getId());// 원본 값 저장
        System.out.println("savedContentEmotionResult.getChoiceEmotion() = " + savedContentEmotionResult.getChoiceEmotion());
        System.out.println("savedContentEmotionResult.getMember().getNickname() = " + savedContentEmotionResult.getMember().getNickname());

        // when
        List<ContentEmotionResultResponseDto> findEmotionResultsByDateAndMember = contentEmotionResultService.getEmotionResultsByDateAndMember(surveyA.getDate(), memberA.getId());

        // then
        Assertions.assertThat(savedContentEmotionResult.getMember().getId()).isEqualTo(findEmotionResultsByDateAndMember.get(0).getMemberId());
        System.out.println("findEmotionResultsByDateAndMember = " + findEmotionResultsByDateAndMember);

    }
}