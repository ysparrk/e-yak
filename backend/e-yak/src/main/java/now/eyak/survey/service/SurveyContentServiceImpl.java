package now.eyak.survey.service;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.Survey;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.*;
import now.eyak.survey.enumeration.SurveyContentType;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        SurveyContentType[] surveyContentTypes = SurveyContentType.values();

        for (int i = 0; i < surveyContentTypes.length; i++) {
            SurveyContent surveyContent = SurveyContent.builder()
                    .surveyContentType(surveyContentTypes[i])
                    .build();

            surveyContent.changeSurvey(survey);
            surveyContentRepository.save(surveyContent);
        }
    }

    @Override
    public List<SurveyContent> getSurveyContentByDate(LocalDate date) {
        return surveyContentRepository.findAllSurveyContentByDate(date);
    }

    @Transactional
    @Override
    public SurveyContentDto getSurveyResultByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        ContentEmotionResultResponseDto emotionResult = contentEmotionResultService.getEmotionResultsByDateAndMember(date, memberId);
        ContentStatusResultResponseDto statusResults = contentStatusResultService.getStatusResultByDateAndMember(date, memberId);
        ContentTextResultResponseDto textResult = contentTextResultService.getTextResultByDateAndMember(date, memberId);

        return SurveyContentDto.builder()
                .contentEmotionResultResponse(emotionResult)
                .contentStatusResultResponse(statusResults)
                .contentTextResultResponse(textResult)
                .build();
    }

    /**
     * 요청받은 날짜에 대한 설문 조회 -> builder에 date추가
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public SurveyContentPdfResponseDto getSurveyResultByDateAndMemberAddDate(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        ContentEmotionResultResponseDto emotionResult = contentEmotionResultService.getEmotionResultsByDateAndMember(date, memberId);
        ContentStatusResultResponseDto statusResults = contentStatusResultService.getStatusResultByDateAndMember(date, memberId);
        ContentTextResultResponseDto textResult = contentTextResultService.getTextResultByDateAndMember(date, memberId);

        return SurveyContentPdfResponseDto.builder()
                .date(date)
                .contentEmotionResultResponse(emotionResult)
                .contentStatusResultResponse(statusResults)
                .contentTextResultResponse(textResult)
                .build();
    }

    /**
     * 요청받은 기간의 설문 내역 조회
     * @param memberId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    @Transactional
    @Override
    public List<SurveyContentPdfResponseDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        List<SurveyContentPdfResponseDto> surveyContentDtoList = new ArrayList<>();

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            SurveyContentPdfResponseDto surveyContentPdfResponseDto = getSurveyResultByDateAndMemberAddDate(date, memberId);

            Long contentEmotionResultId = surveyContentPdfResponseDto.getContentEmotionResultResponse().getContentEmotionResultId();
            Long contentStatusResultId = surveyContentPdfResponseDto.getContentStatusResultResponse().getContentStatusResultId();
            Long contentTextResultId = surveyContentPdfResponseDto.getContentTextResultResponse().getContentTextResultId();

            // 세 설문 모두 비어있을 경우 추가하지 않기
            if (contentEmotionResultId == -1 && contentStatusResultId == -1 && contentTextResultId == -1) {
                continue;
            }
            surveyContentDtoList.add(surveyContentPdfResponseDto);
        }

        return surveyContentDtoList;
    }

}
