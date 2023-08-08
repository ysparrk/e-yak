package now.eyak.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema
@Getter
@Setter
public class SignInDto {
    private String providerName; // naver, kakao, google
    private String token; // Authentication Server 에서 발급받은 token
}
