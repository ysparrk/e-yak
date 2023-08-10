package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.dto.request.ContentTextResultUpdateDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;
import now.eyak.survey.enumeration.SurveyContentType;
import now.eyak.survey.repository.ContentTextResultRepository;
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
class ContentTextResultServiceImplTest {

    @Autowired
    ContentTextResultService contentTextResultService;
    @Autowired
    ContentTextResultRepository contentTextResultRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    SurveyRepository surveyRepository;

    Member member;
    Survey survey;
    SurveyContent surveyContent;
    ContentTextResultDto contentTextResultDto;
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

        surveyContent = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().orElseThrow(() -> new NoSuchElementException("해당하는 날짜에 TEXT 설문 문항이 존재하지 않습니다."));

        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();
    }

    @Test
    @Transactional
    void saveTextSurvey() {
        // given

        // when
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());
        ContentTextResult findContentTextResult = contentTextResultRepository.findById(savedContentTextResult.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentTextResult가 없습니다."));

        // then
        System.out.println("findContentTextResult = " + findContentTextResult);
        System.out.println("savedContentTextResult = " + savedContentTextResult);
        System.out.println("findContentTextResult.equals(savedContentTextResult) = " + findContentTextResult.equals(savedContentTextResult));
        Assertions.assertThat(savedContentTextResult).isEqualTo(findContentTextResult);

    }

    @Test
    @Transactional
    void updateTextSurveyResult() {
        // given
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());  // 원본 값 저장

        System.out.println("savedContentTextResult.getText() = " + savedContentTextResult.getText());

        ContentTextResultUpdateDto contentTextResultUpdateDto = ContentTextResultUpdateDto.builder()
                .contentTextResultId(savedContentTextResult.getId())
                .text("컨디션 수정할게요")
                .build();

        // when
        contentTextResultService.updateTextSurveyResult(contentTextResultUpdateDto, surveyContent.getId(), member.getId());
        ContentTextResult findContentTextResult = contentTextResultRepository.findById(contentTextResultUpdateDto.getContentTextResultId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentTextResult가 존재하지 않습니다."));

        // then
        Assertions.assertThat(findContentTextResult.getText()).isEqualTo(contentTextResultUpdateDto.getText());
        System.out.println("findContentTextResult.getText() = " + findContentTextResult.getText());

    }

    @Test
    @Transactional
    void deleteTextSurveyResult() {
        // given
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());  // 원본 값 저장
        System.out.println("savedContentTextResult.getText() = " + savedContentTextResult.getText());

        // when
        contentTextResultService.deleteTextSurveyResult(savedContentTextResult.getId(), member.getId());

        // then
        Assertions.assertThat(contentTextResultRepository.findById(savedContentTextResult.getId())).isEmpty();
        System.out.println("contentTextResultRepository = " + contentTextResultRepository.findById(savedContentTextResult.getId()));
    }

    @DisplayName("Text GET")
    @Test
    @Transactional
    void getTextResultsByDateAndMember() {
        // given
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());  // 원본 값 저장

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

        Survey surveyA = surveyRepository.findByDate(LocalDate.now()).orElseThrow(() -> new NoSuchElementException("해당 날짜에 설문이 존재하지 않습니다."));

        contentTextResultDto = ContentTextResultDto.builder()
                .text("드민이 입력한 7월 1일 컨디션입니다.")
                .build();

        savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), memberA.getId());  // 원본 값 저장
        System.out.println("savedContentTextResult = " + savedContentTextResult.getText());
        System.out.println("savedContentTextResult = " + savedContentTextResult.getMember().getNickname());

        // when
        ContentTextResultResponseDto findTextResultsByDateAndMember = contentTextResultService.getTextResultByDateAndMember(surveyA.getDate(), memberA.getId());

        // then
        Assertions.assertThat(savedContentTextResult.getMember().getId()).isEqualTo(findTextResultsByDateAndMember.getMemberId());
        System.out.println("findTextResultsByDateAndMember = " + findTextResultsByDateAndMember);

    }
}