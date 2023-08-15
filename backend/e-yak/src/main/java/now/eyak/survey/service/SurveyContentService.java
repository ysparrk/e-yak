package now.eyak.survey.service;

import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.dto.response.SurveyContentPdfResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SurveyContentService {
    SurveyContentDto getSurveyResultByDateAndMember(LocalDate date, Long memberId); // 전체 조회
    void insertSurveyAndSurveyContentPerDay();
    List<SurveyContent> getSurveyContentByDate(LocalDate date);
}
