package now.eyak.survey.service;

import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.response.SurveyContentDto;

import java.time.LocalDate;
import java.util.List;

public interface SurveyContentService {
    List<SurveyContentDto> getSurveyResultByDateAndMember(LocalDate date, Long memberId); // 전체 조회
    void insertSurveyAndSurveyContentPerDay();
    List<SurveyContent> getSurveyContentByDate(LocalDate date);
}
