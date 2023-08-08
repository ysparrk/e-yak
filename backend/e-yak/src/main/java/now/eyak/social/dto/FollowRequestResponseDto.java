package now.eyak.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;
import now.eyak.social.domain.FollowRequest;

@Getter
@Setter
@Builder
public class FollowRequestResponseDto {
    private Long followRequestId;
    private Long followerId;
    private Long followeeId;
    private String followerNickname;
    private String followeeNickname;
    private String customName;
    private Scope scope;

    public static FollowRequestResponseDto from(FollowRequest followRequest) {
        return FollowRequestResponseDto.builder()
                .followRequestId(followRequest.getId())
                .followerId(followRequest.getFollower().getId())
                .followeeId(followRequest.getFollowee().getId())
                .followerNickname(followRequest.getFollower().getNickname())
                .followeeNickname(followRequest.getFollowee().getNickname())
                .customName(followRequest.getCustomName())
                .scope(followRequest.getScope())
                .build();
    }
}
