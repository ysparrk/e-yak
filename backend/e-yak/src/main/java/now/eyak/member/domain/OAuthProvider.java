package now.eyak.member.domain;

import lombok.Getter;

@Getter
public class OAuthProvider {
    private String userInfoUrl;

    public OAuthProvider(OAuthProperties.Provider provider) {
        this.userInfoUrl = provider.getUserInfoUri();
    }
}
