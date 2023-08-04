package now.eyak.member.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OAuthProperties {
    private final Map<String, Provider> provider = new HashMap<>();

    @Getter
    @Setter
    public static class Provider {
        private String jwks;
    }
}