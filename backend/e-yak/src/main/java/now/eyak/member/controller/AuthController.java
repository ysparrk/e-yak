package now.eyak.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.dto.request.ReissueDto;
import now.eyak.member.dto.request.SignInDto;
import now.eyak.member.dto.request.SignUpDto;
import now.eyak.member.dto.response.RefreshResponseDto;
import now.eyak.member.dto.response.SignInResponseDto;
import now.eyak.member.dto.response.SignUpResponseDto;
import now.eyak.member.service.MemberService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final MemberService memberService;
    private final ApiVersionHolder apiVersionHolder;

    @Operation(summary = "Sign In", description = "Open ID Connect에서의 Open Id Token 을 이용해 sign in을 진행한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = SignInResponseDto.class)))
    @PostMapping("/auth/signin")
    public ResponseEntity signIn(@RequestBody SignInDto signInDto) throws Exception {
        log.debug("signIn()");
        SignInResponseDto signInResponseDto = memberService.signIn(signInDto);

        return ResponseEntity.ok(signInResponseDto);
    }

    @Operation(summary = "Sign Up", description = "Open ID Connect에서의 Open Id Token 을 이용해 sign up을 진행한다.")
    @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = SignUpResponseDto.class)))
    @PostMapping("/auth/signup")
    public ResponseEntity signUp(@RequestBody SignUpDto signUpDto) throws Exception {
        log.debug("signUp()");
        Member savedMember = memberService.signUp(signUpDto);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/members/" + savedMember.getId())).build();
    }

    @Operation(summary = "Reissue", description = "Sign In 의 응답값인 Refresh Token을 사용하여 Access Token과 Refresh Token을 재발급 한다. 사용한 Refresh Token은 폐기되며 유효기간은 28일이다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = RefreshResponseDto.class)))
    @PostMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestBody ReissueDto reissueDto) {
        RefreshResponseDto refreshResponseDto = memberService.issueAccessTokenByRefreshToken(reissueDto);

        return ResponseEntity.ok(refreshResponseDto);
    }

    @Operation(summary = "Duplication", description = "사용자의 닉네임을 중복 검사한다.")
    @ApiResponse(responseCode = "200", description = "성공") // TODO: content 추가
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
