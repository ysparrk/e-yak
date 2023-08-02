package now.eyak.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.dto.*;
import now.eyak.member.service.MemberService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final MemberService memberService;
    private final ApiVersionHolder apiVersionHolder;

    @PostMapping("/auth/signin")
    public ResponseEntity signIn(@RequestBody SignInDto signInDto) throws Exception {
        log.debug("signIn()");
        SignInResponseDto signInResponseDto = memberService.signIn(signInDto);

        return ResponseEntity.ok(signInResponseDto);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity signUp(@RequestBody SignUpDto signUpDto) throws Exception {
        log.debug("signUp()");
        Member savedMember = memberService.signUp(signUpDto);

        return ResponseEntity.created( new URI(apiVersionHolder.getVersion() + "/members/" + savedMember.getId())).build();
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestBody ReissueDto reissueDto) {
        RefreshResponseDto refreshResponseDto = memberService.issueAccessTokenByRefreshToken(reissueDto);

        return ResponseEntity.ok(refreshResponseDto);
    }

    @GetMapping("/auth/duplication")
    public ResponseEntity duplicationCheck(@RequestParam String nickname) {
        boolean duplicated = memberService.isDuplicatedNickname(nickname);

        return ResponseEntity.ok(duplicated);
    }

    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.ok("테스트 성공");
    }

}
