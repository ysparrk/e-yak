package now.eyak.member.dto;

import lombok.Builder;
import lombok.Setter;

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
