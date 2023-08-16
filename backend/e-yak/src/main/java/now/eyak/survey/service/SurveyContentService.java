package now.eyak.survey.service;

import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.SurveyContentDto;

import java.time.LocalDate;
import java.util.List;

public interface SurveyContentService {
    SurveyContentDto getSurveyResultByDateAndMember(LocalDate date, Long memberId); // 전체 조회
    List<SurveyContent> insertSurveyAndSurveyContentPerDay(LocalDate date);
    List<SurveyContent> getSurveyContentByDate(LocalDate date);
}
