package now.eyak.social.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;
import now.eyak.social.dto.FollowRequestResponseDto;
import now.eyak.social.service.FollowRequestService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FollowRequestController {
    private final FollowRequestService followRequestService;
    private final ApiVersionHolder apiVersionHolder;

    @PostMapping("/members/{followerId}/follow-requests")
    public ResponseEntity insertFollowRequest(@RequestBody FollowRequestDto followRequestDto, @AuthenticationPrincipal Long memberId) throws URISyntaxException {
        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/members/" + memberId + "/follow-requests/" + followRequest.getId())).build();
    }

    @DeleteMapping("/members/{followerId}/follow-requests/{followRequestId}")
    public ResponseEntity deleteFollowRequest(@PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.declineOrCancelFollowRequest(followRequestId, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/{followId}/follow-requests/{followRequestId}")
    public ResponseEntity acceptFollowRequest(@RequestBody FollowRequestAcceptDto followRequestAcceptDto, @PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequestId, memberId);

        return ResponseEntity.created(null).build();
    }

    @GetMapping("/follow-requests")
    public ResponseEntity getAllFollowRequestByFollowerId(@RequestParam Boolean isGetFollowers, @AuthenticationPrincipal Long memberId) {
        List<FollowRequest> followRequests = null;
        if (isGetFollowers) {
            // 사용자(memberId)에게 요청된 팔로우 요청 전체 조회를 하는 경우
            followRequests = followRequestService.retrieveAllFollowRequestByFolloweeId(memberId);
        } else {
            // 사용자(memberId)가 요청한 팔로우 요청 전체 조회를 하는 경우
            followRequests = followRequestService.retrieveAllFollowRequestByFollowerId(memberId);
        }

        List<FollowRequestResponseDto> followRequestResponseDtoList = followRequests.stream().map(FollowRequestResponseDto::from).toList();

        return ResponseEntity.ok(followRequestResponseDtoList);
    }

}
