package now.eyak.survey.repository;

import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.ContentStatusResultChoiceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentStatusResultChoiceStatusEntityRepository extends
        JpaRepository<ContentStatusResultChoiceStatusEntity, Long> {
    List<ContentStatusResultChoiceStatusEntity> findByContentStatusResult(ContentStatusResult contentStatusResult);
}