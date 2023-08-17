package now.eyak.survey.repository;

import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.query.SurveyContentPdfQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SurveyContentRepository extends JpaRepository<SurveyContent, Long> {
    @Query("select sc from SurveyContent as sc join sc.survey as s on s.date = :date")
    List<SurveyContent> findAllSurveyContentByDate(LocalDate date);
    List<SurveyContentPdfQueryDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime); // 요청받은 기간에 설문한 내역 조회
}
