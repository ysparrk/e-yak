package now.eyak.social.repository;

import now.eyak.member.domain.Member;
import now.eyak.social.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowee(Member followee);
    Optional<Follow> findByIdAndFollower(Long followId, Member follower);
    Optional<Follow> findByIdAndFollowee(Long followId, Member followee);
    void deleteByFolloweeAndFollower(Member followee, Member follower);

}
