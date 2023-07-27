package now.eyak.member.domain.enumeration;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String key;

    Role(String key) {
        this.key = key;
    }
}
