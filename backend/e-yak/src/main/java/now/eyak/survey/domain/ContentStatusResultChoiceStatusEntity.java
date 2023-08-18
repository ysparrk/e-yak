package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@NoArgsConstructor
public class ContentStatusResultChoiceStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private ContentStatusResult contentStatusResult;
    @ManyToOne
    private ChoiceStatusEntity choiceStatusEntity;

    @Builder
    public ContentStatusResultChoiceStatusEntity(Long id, ContentStatusResult contentStatusResult,
                                                 ChoiceStatusEntity choiceStatusEntity) {
        this.id = id;
        this.contentStatusResult = contentStatusResult;
        this.choiceStatusEntity = choiceStatusEntity;
    }

    public static ContentStatusResultChoiceStatusEntity createContentStatusResultChoiceStatusEntity(ContentStatusResult contentStatusResult, ChoiceStatusEntity choiceStatusEntity) {
        ContentStatusResultChoiceStatusEntity contentStatusResultChoiceStatusEntity = ContentStatusResultChoiceStatusEntity.builder()
                .contentStatusResult(contentStatusResult)
                .choiceStatusEntity(choiceStatusEntity)
                .build();

        contentStatusResult.addChoiceStatusEntity(contentStatusResultChoiceStatusEntity);

        return contentStatusResultChoiceStatusEntity;
    }
}
