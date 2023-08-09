package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.enumeration.ChoiceEmotion;

@Getter
@Setter
@Builder
public class ContentEmotionResultDto {
    private ChoiceEmotion choiceEmotion;
}
