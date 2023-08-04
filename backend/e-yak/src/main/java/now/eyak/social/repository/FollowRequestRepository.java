package now.eyak.social.repository;

import now.eyak.social.domain.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

}
