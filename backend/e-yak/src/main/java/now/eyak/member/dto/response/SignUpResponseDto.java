package now.eyak.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema
@Getter
@Setter
public class SignUpResponseDto {
    private String accessToken;

    public SignUpResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
