package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentEmotionResultUpdateDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.enumeration.SurveyContentType;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    @Autowired
    SurveyContentService surveyContentService;

    Member member;
    SurveyContent surveyContent;
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

        surveyContent = surveyContentService.getSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.CHOICE_EMOTION)).findAny().get();

        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.SOSO)
                .build();

    }


    @Test
    @Transactional
    void saveEmotionSurveyResult() {
        // given

        // when
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContent.getId(), member.getId());
        ContentEmotionResult findContentEmotionResult = contentEmotionResultRepository.findById(savedContentEmotionResult.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentEmotionResult가 없습니다."));

        // then
        System.out.println("savedContentEmotionResult = " + savedContentEmotionResult.getChoiceEmotion());
        System.out.println("findContentEmotionResult = " + findContentEmotionResult.getChoiceEmotion());
        Assertions.assertThat(savedContentEmotionResult).isEqualTo(findContentEmotionResult);
    }

    @Test
    @Transactional
    void updateEmotionSurveyResult() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContent.getId(), member.getId()); // 원본 값 저장
        System.out.println("savedContentEmotionResult.getChoiceEmotion() = " + savedContentEmotionResult.getChoiceEmotion());

        ContentEmotionResultUpdateDto contentEmotionResultUpdateDto = ContentEmotionResultUpdateDto.builder()
                .contentEmotionResultId(savedContentEmotionResult.getId())
                .choiceEmotion(ChoiceEmotion.GOOD)  // emotion 수정
                .build();

        // when
        contentEmotionResultService.updateEmotionSurveyResult(contentEmotionResultUpdateDto, surveyContent.getId(), member.getId());
        ContentEmotionResult findContentEmotionResult = contentEmotionResultRepository.findById(contentEmotionResultUpdateDto.getContentEmotionResultId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentEmotionResult가 존재하지 않습니다."));

        // then
        Assertions.assertThat(findContentEmotionResult.getChoiceEmotion()).isEqualTo(contentEmotionResultUpdateDto.getChoiceEmotion());
        System.out.println("findContentEmotionResult.getChoiceEmotion() = " + findContentEmotionResult.getChoiceEmotion());
    }


    @Test
    @Transactional
    void deleteEmotionSurveyResult() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContent.getId(), member.getId());// 원본 값 저장
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
    void getEmotionResultsByDateAndMember() {
        // given
        ContentEmotionResult savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContent.getId(), member.getId());// 원본 값 저장

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

        contentEmotionResultDto = ContentEmotionResultDto.builder()
                .choiceEmotion(ChoiceEmotion.GOOD)
                .build();


        savedContentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContent.getId(), memberA.getId());// 새로운 값 저장
        System.out.println("savedContentEmotionResult.getChoiceEmotion() = " + savedContentEmotionResult.getChoiceEmotion());
        System.out.println("savedContentEmotionResult.getMember().getNickname() = " + savedContentEmotionResult.getMember().getNickname());

        // when
        ContentEmotionResultResponseDto findEmotionResultsByDateAndMember = contentEmotionResultService.getEmotionResultsByDateAndMember(LocalDate.now(), memberA.getId());

        // then
        Assertions.assertThat(savedContentEmotionResult.getMember().getId()).isEqualTo(findEmotionResultsByDateAndMember.getMemberId());
        System.out.println("findEmotionResultsByDateAndMember = " + findEmotionResultsByDateAndMember);
    }
}