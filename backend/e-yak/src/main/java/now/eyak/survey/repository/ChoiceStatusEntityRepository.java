package now.eyak.survey.repository;

import now.eyak.survey.domain.ChoiceStatusEntity;
import now.eyak.survey.enumeration.ChoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChoiceStatusEntityRepository extends JpaRepository<ChoiceStatusEntity, Long> {
    Optional<ChoiceStatusEntity> findByChoiceStatus(ChoiceStatus choiceStatus);
}
