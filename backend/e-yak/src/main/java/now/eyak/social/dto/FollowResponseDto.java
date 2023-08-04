package now.eyak.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.social.Scope;
import now.eyak.social.domain.Follow;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FollowResponseDto {
    private Long id;
    private Long followerId;
    private Long followeeId;
    private String customName;
    private Scope followeeScope;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static FollowResponseDto from(Follow follow) {
        return FollowResponseDto.builder()
                .id(follow.getId())
                .followerId(follow.getFollower().getId())
                .followeeId(follow.getFollowee().getId())
                .customName(follow.getCustomName())
                .followeeScope(follow.getFolloweeScope())
                .createdAt(follow.getCreatedAt())
                .updatedAt(follow.getUpdatedAt())
                .build();
    }

}
