package now.eyak.social.repository;

import now.eyak.member.domain.Member;
import now.eyak.social.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, CustomFollowRepository {
    Optional<Follow> findByIdAndFollowee(Long followId, Member followee);
    Optional<Follow> findByFollowerAndFollowee(Member follower, Member Followee);
}
