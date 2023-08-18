package now.eyak.member.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfile {
    private String providerId; // ex) naver_ajskdfjoiwerjlk121o3j123
    private String providerName;

    @Builder
    public MemberProfile(String providerId, String providerName) {
        this.providerId = providerId;
        this.providerName = providerName;
    }

    public Member toMember() {
        return Member.builder()
                .providerId(this.getProviderId())
                .providerName(this.getProviderName())
                .build();
    }
}
