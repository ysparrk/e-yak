package now.eyak.survey.repository;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentTextResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentTextResultRepository extends JpaRepository<ContentTextResult, Long> {
    Optional<ContentTextResult> findByIdAndMember(Long id, Member member); // update
    void deleteByIdAndMember(Long contextTextResultId, Member member); // delete
}
