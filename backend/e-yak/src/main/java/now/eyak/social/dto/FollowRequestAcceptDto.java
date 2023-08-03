package now.eyak.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;

@Getter
@Setter
@Builder
public class FollowRequestAcceptDto {
    private Scope scope;
    private String customName;
}
