package now.eyak.survey.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import now.eyak.survey.enumeration.ChoiceStatus;


@Getter
@Entity
@NoArgsConstructor
public class ChoiceStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ChoiceStatus choiceStatus;

    @Builder
    public ChoiceStatusEntity(Long id, ChoiceStatus choiceStatus) {
        this.id = id;
        this.choiceStatus = choiceStatus;
    }
}
