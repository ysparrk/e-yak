package now.eyak.survey.repository;

import java.util.Optional;
import now.eyak.survey.domain.ChoiceStatusEntity;
import now.eyak.survey.enumeration.ChoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceStatusEntityRepository extends JpaRepository<ChoiceStatusEntity, Long> {
    Optional<ChoiceStatusEntity> findByChoiceStatus(ChoiceStatus choiceStatus);
}
