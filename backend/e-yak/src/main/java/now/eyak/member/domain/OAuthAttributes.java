package now.eyak.member.domain;

import now.eyak.member.exception.UnsupportedProviderException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        return MemberProfile.builder()
                .providerId("google_" + (String) (attributes.get("id")))
                .providerName("google")
                .build();
    }),
    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return MemberProfile.builder()
                .providerId("naver_" + (String) (attributes.get("id")))
                .providerName("naver")
                .build();
    }),
    KAKAO("kakao", (attributes) -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return MemberProfile.builder()
                .providerId("kakao_" + (String) (attributes.get("id")))
                .providerName("kakao")
                .build();
    });

    private final String providerName;
    private final Function<Map<String, Object>, MemberProfile> of;

    OAuthAttributes(String providerName, Function<Map<String, Object>, MemberProfile> of) {
        this.providerName = providerName;
        this.of = of;
    }

    public static MemberProfile extract(String providerName, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> provider.providerName.equals(providerName))
                .findFirst()
                .orElseThrow(UnsupportedProviderException::new)
                .of
                .apply(attributes);
    }
}
