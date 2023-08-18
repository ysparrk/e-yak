package now.eyak.member.domain;

import java.util.Map;
import java.util.TreeMap;

public class OAuthAdapter {

    private OAuthAdapter() {
    }

    ;

    public static Map<String, OAuthProvider> getOAuthProviders(OAuthProperties properties) {
        Map<String, OAuthProvider> oAuthProviders = new TreeMap<>();

        properties.getProvider().forEach(((s, provider) -> {
            oAuthProviders.put(s, new OAuthProvider(provider));
        }));

        return oAuthProviders;
    }
}
