package now.eyak.survey.repository;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentStatusResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentStatusResultRepository extends JpaRepository<ContentStatusResult, Long> {
    Optional<ContentStatusResult> findByIdAndMember(Long id, Member member);
}

