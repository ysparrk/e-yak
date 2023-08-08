package now.eyak.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshResponseDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    public RefreshResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
