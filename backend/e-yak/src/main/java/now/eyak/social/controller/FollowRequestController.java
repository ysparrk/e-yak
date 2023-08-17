package now.eyak.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Request Follow", description = "사용자(followerId)가 상대(followeeId)에게 팔로우를 요청합니다.")
    @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = FollowRequestDto.class)))
    @PostMapping("/members/{followerId}/follow-requests")
    public ResponseEntity insertFollowRequest(@RequestBody FollowRequestDto followRequestDto, @AuthenticationPrincipal Long memberId) throws URISyntaxException {
        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/members/" + memberId + "/follow-requests/" + followRequest.getId())).build();
    }

    @Operation(summary = "Refuse Follow", description = "상대가(followerId) 요청한 팔로우(followRequestId)를 사용자가 거절합니다./사용자가 상대에게 요청한 팔로우(followRequestId)를 취소합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowRequestDto.class)))
    @DeleteMapping("/members/{followerId}/follow-requests/{followRequestId}")
    public ResponseEntity deleteFollowRequest(@PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.declineOrCancelFollowRequest(followRequestId, memberId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Accept Follow", description = "상대가(followerId) 요청한 팔로우(followRequestId)를 사용자가 수락합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowRequestAcceptDto.class)))
    @PostMapping("/members/{followId}/follow-requests/{followRequestId}")
    public ResponseEntity acceptFollowRequest(@RequestBody FollowRequestAcceptDto followRequestAcceptDto, @PathVariable Long followRequestId, @AuthenticationPrincipal Long memberId) {
        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequestId, memberId);

        return ResponseEntity.created(null).build();
    }

    @Operation(summary = "Get All Follow Requests", description = "사용자가/에게 요청한/요청된 팔로우 요청을 전체 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowRequestResponseDto.class)))
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
