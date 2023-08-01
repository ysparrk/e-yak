package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentStatusResultDto;
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
}