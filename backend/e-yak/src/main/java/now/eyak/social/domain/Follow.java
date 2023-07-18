package now.eyak.social.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.social.Scope;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * follwer가 follo
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
    private Member follower;
    @ManyToOne
    private Member followee;
    private String customName;
    @Enumerated(EnumType.STRING)
    private Scope scope;
    @CreationTimestamp
    private Timestamp followedAt;
}