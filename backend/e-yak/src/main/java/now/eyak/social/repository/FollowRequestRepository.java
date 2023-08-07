package now.eyak.social.repository;

import now.eyak.member.domain.Member;
import now.eyak.social.domain.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    List<FollowRequest> findByFollower(Member follower);
    List<FollowRequest> findByFollowee(Member followee);
}
