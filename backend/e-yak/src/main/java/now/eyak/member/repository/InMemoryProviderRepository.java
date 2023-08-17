package now.eyak.member.repository;

import now.eyak.member.domain.OAuthProvider;
import now.eyak.member.exception.UnsupportedProviderException;

import java.util.Map;
import java.util.TreeMap;

public class InMemoryProviderRepository {
    private final Map<String, OAuthProvider> providers;

    public InMemoryProviderRepository(Map<String, OAuthProvider> providers) {
        this.providers = new TreeMap<>(providers);
    }

    public OAuthProvider findByProviderName(String name) {
        if (!providers.containsKey(name)) {
            throw new UnsupportedProviderException("지원하지 않는 provider 입니다.");
        }

        return providers.get(name);
    }
}
