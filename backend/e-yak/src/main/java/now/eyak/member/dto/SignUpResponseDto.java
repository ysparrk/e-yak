package now.eyak.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private String accessToken;

    public SignUpResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
