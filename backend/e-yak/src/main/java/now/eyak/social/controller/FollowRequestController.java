package now.eyak.social.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;
import now.eyak.social.service.FollowRequestService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class FollowRequestController {
    private final FollowRequestService followRequestService;
    private final ApiVersionHolder apiVersionHolder;

    @PostMapping("/{followerId}/follow-requests")
    public ResponseEntity insertFollowRequest(@RequestBody FollowRequestDto followRequestDto, @AuthenticationPrincipal Long memberId) throws URISyntaxException {
        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/members/" + memberId + "/follow-requests/" + followRequest.getId())).build();
    }

    @DeleteMapping("/{followerId}/follow-requests/{followRequestId}")
    public ResponseEntity deleteFollowRequest(@PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.declineOrCancelFollowRequest(followRequestId, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{followId}/follow-requests/{followRequestId}")
    public ResponseEntity acceptFollowRequest(@RequestBody FollowRequestAcceptDto followRequestAcceptDto, @PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequestId, memberId);

        return ResponseEntity.created(null).build();
    }

}
