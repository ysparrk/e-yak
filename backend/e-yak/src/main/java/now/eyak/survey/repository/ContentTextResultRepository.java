package now.eyak.survey.repository;

import now.eyak.survey.domain.ContentTextResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentTextResultRepository extends JpaRepository<ContentTextResult, Long> {

    // SurveyContentId에 해당하는 ContentTextResult 조회 메서드 추가
    List<ContentTextResult> findBySurveyContentId(Long surveyContentId);
}
