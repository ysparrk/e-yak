package now.eyak.social.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.social.Scope;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * follwer가 followee를 follow한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member follower;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member followee;
    private String customName;
    @Enumerated(EnumType.STRING)
    private Scope followeeScope; // Followee가 Follower에게 공개할 정보의 범위
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public Follow(Member follower, Member followee, String customName, Scope followeeScope) {
        this.follower = follower;
        this.followee = followee;
        this.customName = customName;
        this.followeeScope = followeeScope;
    }
}
