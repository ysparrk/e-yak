package now.eyak.survey.repository;

import now.eyak.survey.domain.SurveyContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyContentRepository extends JpaRepository<SurveyContent, Long> {
}
