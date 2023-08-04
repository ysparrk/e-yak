package now.eyak.member.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OIDCKey {
    private String e;
    private String alg;
    private String n;
    private String kty;
    private String use;
    private String kid;
}
