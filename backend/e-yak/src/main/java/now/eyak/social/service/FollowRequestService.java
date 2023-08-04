package now.eyak.social.service;

import now.eyak.social.domain.Follow;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;

public interface FollowRequestService {
    FollowRequest insertFollowRequest(FollowRequestDto followRequestDto, Long followerId);
    void declineOrCancelFollowRequest(Long followRequestId, Long memberId);
    Follow acceptFollowRequest(FollowRequestAcceptDto followRequestAcceptDto, Long followRequestId, Long followeeId);
}
