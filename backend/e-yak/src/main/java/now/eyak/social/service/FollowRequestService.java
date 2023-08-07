package now.eyak.social.service;

import now.eyak.social.domain.Follow;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;

import java.util.List;

public interface FollowRequestService {
    FollowRequest insertFollowRequest(FollowRequestDto followRequestDto, Long followerId);
    void declineOrCancelFollowRequest(Long followRequestId, Long memberId);
    Follow acceptFollowRequest(FollowRequestAcceptDto followRequestAcceptDto, Long followRequestId, Long followeeId);
    List<FollowRequest> retrieveAllFollowRequestByFollowerId(Long followerId);
    List<FollowRequest> retrieveAllFollowRequestByFolloweeId(Long followeeId);
}
