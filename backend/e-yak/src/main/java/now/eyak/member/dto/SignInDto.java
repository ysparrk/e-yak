package now.eyak.member.dto;

import lombok.Getter;

@Getter
public class SignInDto {
    private String providerName; // naver, kakao, google
    private String token; // Authentication Server 에서 발급받은 token
}
