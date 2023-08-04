package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SurveyContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Survey survey;
    @OneToMany(mappedBy = "surveyContent")
    private List<ContentChoiceItem> contentChoiceItems;
    private String question;
}
