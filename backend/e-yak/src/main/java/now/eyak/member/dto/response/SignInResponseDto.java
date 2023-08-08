package now.eyak.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.member.dto.request.MemberDto;

@Schema
@Getter
@Setter
public class SignInResponseDto {
    private String accessToken;
    private String refreshToken;
    private MemberDto memberDto;

    @Builder
    public SignInResponseDto(String accessToken, String refreshToken, MemberDto memberDto) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberDto = memberDto;
    }
}
