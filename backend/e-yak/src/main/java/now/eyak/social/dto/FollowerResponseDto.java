package now.eyak.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;
import now.eyak.social.domain.Follow;

@Getter
@Setter
@Builder
public class FollowerResponseDto {
    private Long followId;
    private Long memberId;
    private String nickname;
    @JsonProperty("custom_name")
    private String customName;
    private Scope scope;

    public static FollowerResponseDto from(Follow follow) {
        return FollowerResponseDto.builder()
                .followId(follow.getId())
                .memberId(follow.getFollower().getId())
                .nickname(follow.getFollower().getNickname())
                .customName(follow.getCustomName())
                .scope(follow.getFolloweeScope())
                .build();
    }
}
