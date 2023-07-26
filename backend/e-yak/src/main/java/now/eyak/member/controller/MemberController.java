package now.eyak.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.dto.MemberDto;
import now.eyak.member.dto.MemberUpdateDto;
import now.eyak.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/members/{memberId}")
    public ResponseEntity update(@RequestBody MemberUpdateDto memberUpdateDto, @AuthenticationPrincipal Long memberId) {
        log.debug("update() memberId: {}", memberId);

        MemberDto memberDto = memberService.updateMember(memberUpdateDto, memberId);

        return ResponseEntity.ok(memberDto);
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity delete(@AuthenticationPrincipal Long memberId) {
        log.debug("delete() memberId: {}", memberId);

        memberService.deleteMember(memberId);

        return ResponseEntity.ok().build();
    }
}
