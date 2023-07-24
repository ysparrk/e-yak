package now.eyak.member.controller;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.*;
import now.eyak.member.service.MemberService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;
    private final ApiVersionHolder apiVersionHolder;

    public MemberController(MemberService memberService, ApiVersionHolder apiVersionHolder) {
        this.memberService = memberService;
        this.apiVersionHolder = apiVersionHolder;
    }

    @PostMapping("/auth/signIn")
    public ResponseEntity signIn(@RequestBody SignInDto signInDto) {
        SignInResponseDto signInResponseDto = memberService.signIn(signInDto);

        return ResponseEntity.ok(signInResponseDto);
    }

    @PostMapping("/auth/signUp")
    public ResponseEntity signUp(@RequestBody SignUpDto signUpDto) throws URISyntaxException {
        Member savedMember = memberService.signUp(signUpDto);

        return ResponseEntity.created( new URI(apiVersionHolder.getVersion() + "/members/" + savedMember.getId())).build();
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestBody ReissueDto reissueDto) {
        RefreshResponseDto refreshResponseDto = memberService.issueAccessTokenByRefreshToken(reissueDto);

        return ResponseEntity.ok(refreshResponseDto);
    }
}
