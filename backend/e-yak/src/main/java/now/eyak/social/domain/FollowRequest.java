package now.eyak.social.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.social.Scope;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FollowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member follower;
    @ManyToOne
    private Member followee;
    private String customName;
    @Enumerated(EnumType.STRING)
    private Scope scope;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public FollowRequest(Member follower, Member followee, String customName, Scope scope) {
        this.follower = follower;
        this.followee = followee;
        this.customName = customName;
        this.scope = scope;
    }
}
