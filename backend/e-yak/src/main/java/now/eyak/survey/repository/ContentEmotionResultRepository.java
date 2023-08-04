package now.eyak.survey.repository;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentEmotionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentEmotionResultRepository extends JpaRepository<ContentEmotionResult, Long> {
    Optional<ContentEmotionResult> findByIdAndMember(Long id, Member member); // update
    void deleteByIdAndMember(Long contentEmotionResultId, Member member); // delete
}
