package now.eyak.social.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/{memberId}/follwers")
    public ResponseEntity getFollowers(@AuthenticationPrincipal Long memberId) {
        List<Follow> followers = followService.findFollowers(memberId);
        List<FollowerResponseDto> followerResponseDtoList = followers.stream().map(FollowerResponseDto::of).toList();

        return ResponseEntity.ok(followerResponseDtoList);
    }

    @DeleteMapping("/{memberId}/follows/{followId}")
    public ResponseEntity deleteFollow(@PathVariable Long followId, @AuthenticationPrincipal Long memberId) {
        followService.deleteFollowBi(followId, memberId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{memberId}/follows/{followId}")
    public ResponseEntity updateFollow(@RequestBody FollowUpdateDto followUpdateDto, @PathVariable Long followId, @AuthenticationPrincipal Long memberId) {
        Follow follow = followService.updateFollow(followUpdateDto, followId, memberId);

        return ResponseEntity.ok(FollowResponseDto.from(follow));
    }

}
