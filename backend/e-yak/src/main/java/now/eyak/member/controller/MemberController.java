package now.eyak.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.dto.request.MemberDto;
import now.eyak.member.dto.request.MemberUpdateDto;
import now.eyak.member.dto.response.RefreshResponseDto;
import now.eyak.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Retrieve Member", description = "사용자의 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    @GetMapping("/{memberId}")
    public ResponseEntity retrieve(@AuthenticationPrincipal Long memberId) {
        log.debug("retrieve() memberId: {}", memberId);

        MemberDto memberDto = memberService.retrieveMember(memberId);

        return ResponseEntity.ok(memberDto);
    }

    @Operation(summary = "Update Member", description = "사용자의 정보를 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MemberDto.class)))
    @PutMapping("/{memberId}")
    public ResponseEntity update(@RequestBody MemberUpdateDto memberUpdateDto, @AuthenticationPrincipal Long memberId) {
        log.debug("update() memberId: {}", memberId);

        MemberDto memberDto = memberService.updateMember(memberUpdateDto, memberId);

        return ResponseEntity.ok(memberDto);
    }

    @Operation(summary = "Delete Member", description = "사용자의 정보를 삭제한다.(회원 탈퇴)")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MemberDto.class)))  // content 확인
    @DeleteMapping("/{memberId}")
    public ResponseEntity delete(@AuthenticationPrincipal Long memberId) {
        log.debug("delete() memberId: {}", memberId);

        memberService.deleteMember(memberId);

        return ResponseEntity.ok().build();
    }
}
