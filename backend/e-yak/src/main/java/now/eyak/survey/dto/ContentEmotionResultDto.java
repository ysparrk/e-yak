package now.eyak.survey.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.enumeration.ChoiceEmotion;

@Getter
@Setter
@Builder
public class ContentEmotionResultDto {
    private Long id;
    private Long surveyContentId;
    private ChoiceEmotion choiceEmotion;

    public ContentEmotionResult update(ContentEmotionResult contentEmotionResult) {
        contentEmotionResult.setChoiceEmotion(choiceEmotion);

        return contentEmotionResult;
    }

}
