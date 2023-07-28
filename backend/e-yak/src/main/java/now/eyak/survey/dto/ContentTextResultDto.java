package now.eyak.survey.dto;

import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.SurveyContent;

@Getter
@Setter
public class ContentTextResultDto {
    private String text;
    private Long surveyContentId;  // surveycontent의 id
}
