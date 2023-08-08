package now.eyak.survey.repository;

import now.eyak.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findByDate(LocalDate date);
}
