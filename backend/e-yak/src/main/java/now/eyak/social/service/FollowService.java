package now.eyak.social.service;

import now.eyak.social.domain.Follow;
import now.eyak.social.dto.FollowUpdateDto;
import now.eyak.social.dto.FollowerResponseDto;

import java.util.List;

public interface FollowService {
    List<FollowerResponseDto> findFollowers(Long memberId);

    void deleteFollowBi(Long followId, Long memberId);

    Follow updateFollow(FollowUpdateDto followUpdateDto, Long followId, Long memberId);
}
