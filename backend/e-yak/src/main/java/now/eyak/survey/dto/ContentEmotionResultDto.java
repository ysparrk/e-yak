package now.eyak.survey.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.enumeration.ChoiceEmotion;

@Getter
@Setter
@Builder
public class ContentEmotionResultDto {
    private Long id;
    private Long surveyContentId;
    private ChoiceEmotion choiceEmotion;

}
