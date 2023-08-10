package now.eyak.social.repository;

import now.eyak.member.domain.Member;
import now.eyak.social.dto.FollowResponseDto;

import java.util.List;

public interface CustomFollowRepository {
    List<FollowResponseDto> findFollowByFollowee(Member followee);
}
