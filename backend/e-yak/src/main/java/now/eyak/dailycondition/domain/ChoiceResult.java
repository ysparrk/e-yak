package now.eyak.dailycondition.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChoiceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ChoiceItem choiceItem;
    @ManyToOne
    private Member member;
    private Timestamp createdAt;
}
