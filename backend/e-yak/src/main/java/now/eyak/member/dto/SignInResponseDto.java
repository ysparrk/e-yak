package now.eyak.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
