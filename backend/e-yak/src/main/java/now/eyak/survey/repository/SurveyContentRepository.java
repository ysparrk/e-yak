package now.eyak.survey.repository;

import now.eyak.survey.domain.SurveyContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SurveyContentRepository extends JpaRepository<SurveyContent, Long> {
    @Query("select sc from SurveyContent as sc join sc.survey as s on s.date = :date")
    List<SurveyContent> findAllSurveyContentByDate(LocalDate date);
}
