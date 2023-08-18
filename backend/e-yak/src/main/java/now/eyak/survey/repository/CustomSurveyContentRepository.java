package now.eyak.survey.repository;

import now.eyak.survey.dto.query.SurveyContentPdfQueryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomSurveyContentRepository {
    List<SurveyContentPdfQueryDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime); // 요청받은 기간에 설문한 내역 조회
}

