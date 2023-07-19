package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "survey")
    private List<SurveyContent> surveyContents;
}
