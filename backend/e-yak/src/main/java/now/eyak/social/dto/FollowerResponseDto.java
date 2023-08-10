package now.eyak.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;
import now.eyak.social.domain.Follow;

@Getter
@Setter
public class FollowerResponseDto {
    private Long followId;
    private Long memberId;
    private String nickname;
    @JsonProperty("custom_name")
    private String customName;
    private Scope scope;

    @Builder
    public FollowerResponseDto(Long followId, Long memberId, String nickname, String customName, Scope scope) {
        this.followId = followId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.customName = customName;
        this.scope = scope;
    }

    public static FollowerResponseDto of(Follow follow) {
        return FollowerResponseDto.builder()
                .followId(follow.getId())
                .memberId(follow.getFollower().getId())
                .nickname(follow.getFollower().getNickname())
                .customName(follow.getCustomName())
                .scope(follow.getFolloweeScope())
                .build();
    }
}
