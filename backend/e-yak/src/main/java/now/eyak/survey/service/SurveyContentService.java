package now.eyak.survey.service;

import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.dto.response.SurveyContentPdfResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SurveyContentService {
    SurveyContentDto getSurveyResultByDateAndMember(LocalDate date, Long memberId); // 전체 조회

    List<SurveyContent> insertSurveyAndSurveyContentPerDay(LocalDate date);

    List<SurveyContent> getSurveyContentByDate(LocalDate date);

    SurveyContentPdfResponseDto getSurveyResultByDateAndMemberAddDate(LocalDate date, Long memberId); // 전체조회 + 날짜 추가

    List<SurveyContentPdfResponseDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime); // 요청받은 기간에 설문한 내역 조회
}
