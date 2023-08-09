package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.survey.enumeration.SurveyContentType;

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
    @Enumerated(EnumType.STRING)
    private SurveyContentType surveyContentType;

    @OneToMany(mappedBy = "surveyContent")
    private List<ContentEmotionResult> contentEmotionResults = new ArrayList<>();  // emotion 설문
    @OneToMany(mappedBy = "surveyContent")
    private List<ContentStatusResult> contentStatusResults = new ArrayList<>();  // status 설문
    @OneToMany(mappedBy = "surveyContent")
    private List<ContentTextResult> contentTextResult = new ArrayList<>();  // text 설문

    @Builder
    public SurveyContent(Long id, Survey survey, SurveyContentType surveyContentType, List<ContentEmotionResult> contentEmotionResults, List<ContentStatusResult> contentStatusResults, List<ContentTextResult> contentTextResult) {
        this.id = id;
        this.survey = survey;
        this.surveyContentType = surveyContentType;
        this.contentEmotionResults = contentEmotionResults;
        this.contentStatusResults = contentStatusResults;
        this.contentTextResult = contentTextResult;
    }

    public void changeSurvey(Survey survey) {
        this.survey = survey;
        survey.getSurveyContents().add(this);
    }
}
