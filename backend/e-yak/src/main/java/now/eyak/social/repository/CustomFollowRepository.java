package now.eyak.social.repository;

import now.eyak.member.domain.Member;
import now.eyak.social.dto.FollowerResponseDto;

import java.util.List;

public interface CustomFollowRepository {
    List<FollowerResponseDto> findFollowByFollowee(Member followee);
}
