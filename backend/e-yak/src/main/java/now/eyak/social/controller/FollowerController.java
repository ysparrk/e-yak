package now.eyak.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.social.domain.Follow;
import now.eyak.social.dto.FollowResponseDto;
import now.eyak.social.dto.FollowUpdateDto;
import now.eyak.social.dto.FollowerResponseDto;
import now.eyak.social.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class FollowerController {
    private final FollowService followService;

    @Operation(summary = "Get All Followers", description = "사용자의 팔로워를 전체 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowerResponseDto.class)))
    @GetMapping("/{memberId}/follwers")
    public ResponseEntity getFollowers(@AuthenticationPrincipal Long memberId) {
        List<Follow> followers = followService.findFollowers(memberId);
        List<FollowerResponseDto> followerResponseDtoList = followers.stream().map(FollowerResponseDto::of).toList();

        return ResponseEntity.ok(followerResponseDtoList);
    }

    @Operation(summary = "Delete Followings", description = "사용자가 상대 팔로우를 삭제하며 동시에 팔로워에서 삭제한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowerResponseDto.class)))
    @DeleteMapping("/{memberId}/follows/{followId}")
    public ResponseEntity deleteFollow(@PathVariable Long followId, @AuthenticationPrincipal Long memberId) {
        followService.deleteFollowBi(followId, memberId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Modify Follows", description = "사용자가 팔로워에 공개할 정보의 범위와 별칭을 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FollowUpdateDto.class)))
    @PatchMapping("/{memberId}/follows/{followId}")
    public ResponseEntity updateFollow(@RequestBody FollowUpdateDto followUpdateDto, @PathVariable Long followId, @AuthenticationPrincipal Long memberId) {
        Follow follow = followService.updateFollow(followUpdateDto, followId, memberId);

        return ResponseEntity.ok(FollowResponseDto.from(follow));
    }

}
