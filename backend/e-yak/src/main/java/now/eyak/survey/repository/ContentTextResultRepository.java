package now.eyak.survey.repository;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.SurveyContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentTextResultRepository extends JpaRepository<ContentTextResult, Long> {
    Optional<ContentTextResult> findByIdAndMember(Long id, Member member); // update

    Optional<ContentTextResult> findBySurveyContentAndMember(SurveyContent surveyContent, Member member);

    void deleteByIdAndMember(Long contextTextResultId, Member member); // delete
}
