package now.eyak.survey.repository;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.SurveyContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentStatusResultRepository extends JpaRepository<ContentStatusResult, Long> {
    Optional<ContentStatusResult> findByIdAndMember(Long id, Member member); // update

    Optional<ContentStatusResult> findBySurveyContentAndMember(SurveyContent surveyContent, Member member);
    void deleteByIdAndMember(Long contentStatusResultId, Member member); // delete
}

