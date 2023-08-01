package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentStatusResultDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.enumeration.ChoiceStatus;
import now.eyak.survey.repository.ContentStatusResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.NoSuchElementException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContentStatusResultServiceImplTest {

    @Autowired
    ContentStatusResultService contentStatusResultService;
    @Autowired
    ContentStatusResultRepository contentStatusResultRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    SurveyRepository surveyRepository;

    Member member;
    Survey survey;
    SurveyContent surveyContent;
    ContentStatusResultDto contentStatusResultDto;

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
                .build();

        survey = surveyRepository.save(survey);

        surveyContent = SurveyContent.builder()
                .survey(survey)
                .question("오늘 몸상태가 어떠신가요? :)")
                .build();

        surveyContent = surveyContentRepository.save(surveyContent);


        contentStatusResultDto = ContentStatusResultDto.builder()
                .selectedStatusChoices(Arrays.asList(ChoiceStatus.HEADACHE, ChoiceStatus.NAUSEA, ChoiceStatus.FEVER))
                .surveyContentId(surveyContent.getId())
                .build();

    }

    @Test
    @Transactional
    @Rollback(false)
    void saveStatusSurveyResult() {
        // given

        // when
        ContentStatusResult savedContentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, member.getId());
        ContentStatusResult findContentStatusResult = contentStatusResultRepository.findById(savedContentStatusResult.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentStatusResult가 없습니다."));

        // then
        System.out.println("savedContentStatusResult = " + savedContentStatusResult.getSelectedStatusChoices());
        System.out.println("findContentStatusResult = " + findContentStatusResult.getSelectedStatusChoices());
        Assertions.assertThat(savedContentStatusResult).isEqualTo(findContentStatusResult);
    }

    @Test
    @Transactional
    @Rollback(false)
    void updateStatusSurveyResult() {
        // given
        ContentStatusResult savedContentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, member.getId());  // 원본 값 저장
        System.out.println("savedContentStatusResult = " + savedContentStatusResult.getSelectedStatusChoices());
        System.out.println("savedContentStatusResult.getId() = " + savedContentStatusResult.getId());

        // 새로운 값 저장
        ContentStatusResultDto contentStatusResultDto = ContentStatusResultDto.builder()
                .id(savedContentStatusResult.getId())
                .selectedStatusChoices(Arrays.asList(ChoiceStatus.HEADACHE, ChoiceStatus.COUGH, ChoiceStatus.VOMITING, ChoiceStatus.ABDOMINAL_PAIN))  // HEADACHE 제외한 다른 증상으로 변경
                .surveyContentId(surveyContent.getId())
                .build();
        // when
        contentStatusResultService.updateStatusSurveyResult(contentStatusResultDto, member.getId());
        ContentStatusResult findContentStatusResult = contentStatusResultRepository.findById(contentStatusResultDto.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentStatusResult가 존재하지 않습니다."));

        // then
        System.out.println("findContentStatusResult = " + findContentStatusResult.getSelectedStatusChoices());
        System.out.println("findContentStatusResult.getId() = " + findContentStatusResult.getId());
        Assertions.assertThat(findContentStatusResult.getSelectedStatusChoices()).isEqualTo(contentStatusResultDto.getSelectedStatusChoices());

    }

}