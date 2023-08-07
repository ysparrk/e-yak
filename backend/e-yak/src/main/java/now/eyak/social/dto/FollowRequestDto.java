package now.eyak.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;
import now.eyak.social.domain.FollowRequest;

@Getter
@Setter
@Builder
public class FollowRequestDto {
    private Scope followerScope;
    private String followeeNickname;
    private String customName;

    public void update(FollowRequest followRequest) {
        followRequest.setScope(followerScope);
        followRequest.setCustomName(customName);
    }
}
