package now.eyak.survey.service;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SurveyContentServiceImpl implements SurveyContentService {

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyContentRepository surveyContentRepository;

    private final ContentEmotionResultService contentEmotionResultService;
    private final ContentStatusResultService contentStatusResultService;
    private final ContentTextResultService contentTextResultService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
    public void insertSurveyAndSurveyContentPerDay() {
        Survey survey = Survey.builder()
                .date(LocalDate.now())
                .build();

        surveyRepository.save(survey);

        SurveyContent surveyContent = SurveyContent.builder()
                .build();

        surveyContent.changeSurvey(survey);
        surveyContentRepository.save(surveyContent);
    }

    @Override
    public List<SurveyContent> getSurveyContentByDate(LocalDate date) {
        return surveyContentRepository.findAllSurveyContentByDate(date);
    }

    @Transactional
    @Override
    public List<SurveyContentDto> getSurveyResultByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        Survey survey = surveyRepository.findByDate(date).orElseThrow(() -> new NoSuchElementException("해당하는 날짜의 설문기록이 없습니다."));

        List<ContentEmotionResultResponseDto> emotionResult = contentEmotionResultService.getEmotionResultsByDateAndMember(date, memberId);
        List<ContentStatusResultResponseDto> statusResults = contentStatusResultService.getStatusResultsByDateAndMember(date, memberId);
        List<ContentTextResultResponseDto> textResult = contentTextResultService.getTextResultsByDateAndMember(date, memberId);

        List<SurveyContentDto> surveyContentResponseList = new ArrayList<>();
        SurveyContentDto surveyContentDto = SurveyContentDto.builder()
                .contentEmotionResultResponses(emotionResult)
                .contentStatusResultResponses(statusResults)
                .contentTextResultResponse(textResult)
                .build();

        surveyContentResponseList.add(surveyContentDto);


        return surveyContentResponseList;
    }

}
