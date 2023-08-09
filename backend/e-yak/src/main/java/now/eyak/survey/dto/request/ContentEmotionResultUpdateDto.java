package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.enumeration.ChoiceEmotion;

@Getter
@Setter
@Builder
public class ContentEmotionResultUpdateDto {
    private Long contentEmotionResultId;
    private ChoiceEmotion choiceEmotion;
    public void update(ContentEmotionResult contentEmotionResult) {
        contentEmotionResult.setChoiceEmotion(choiceEmotion);
    }
}
