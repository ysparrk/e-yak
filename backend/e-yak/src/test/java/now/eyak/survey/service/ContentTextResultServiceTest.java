package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.repository.ContentTextResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.NoSuchElementException;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContentTextResultServiceTest {

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

    @Test
    @Transactional
    @Rollback(false)
    void saveTextSurvey() {
        // given
        Member member = Member.builder()
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

        Survey survey = Survey.builder()
                .build();

        survey = surveyRepository.save(survey);

        SurveyContent surveyContent = SurveyContent.builder()
                .survey(survey)
                .question("오늘 몸상태가 어떠신가요? :)")
                .build();

        surveyContent = surveyContentRepository.save(surveyContent);

        ContentTextResultDto contentTextResultDto = ContentTextResultDto.builder()
                .text("빌더 싫음")
                .surveyContentId(surveyContent.getId())
                .build();

        // when
        ContentTextResult savedContentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, member.getId());
        ContentTextResult findContentTextResult = contentTextResultRepository.findById(savedContentTextResult.getId()).orElseThrow(() -> new NoSuchElementException("해당하는 contentTextResult가 없습니다."));

        // then
        System.out.println("findContentTextResult = " + findContentTextResult);
        System.out.println("savedContentTextResult = " + savedContentTextResult);
        System.out.println("findContentTextResult.equals(savedContentTextResult) = " + findContentTextResult.equals(savedContentTextResult));
        Assertions.assertThat(savedContentTextResult).isEqualTo(findContentTextResult);

    }

}