package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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
    private List<ContentEmotionResult> contentEmotionResults = new ArrayList<>();  // emotion 설문
    @OneToMany(mappedBy = "surveyContent")
    private List<ContentStatusResult> contentStatusResults = new ArrayList<>();  // status 설문
    @OneToMany(mappedBy = "surveyContent")
    private List<ContentTextResult> contentTextResult = new ArrayList<>();  // text 설문

    @Builder
    public SurveyContent(Survey survey, List<ContentEmotionResult> contentEmotionResults, List<ContentStatusResult> contentStatusResults, List<ContentTextResult> contentTextResult) {
        this.survey = survey;
        this.contentEmotionResults = contentEmotionResults;
        this.contentStatusResults = contentStatusResults;
        this.contentTextResult = contentTextResult;
    }

    public void changeSurvey(Survey survey) {
        this.survey = survey;
        survey.getSurveyContents().add(this);
    }
}
