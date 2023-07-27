package now.eyak.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDto {
    private String providerName; // naver, kakao, google
    private String token; // Authentication Server 에서 발급받은 token
}
