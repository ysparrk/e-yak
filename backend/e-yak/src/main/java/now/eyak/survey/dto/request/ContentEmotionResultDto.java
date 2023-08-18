package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.survey.enumeration.ChoiceEmotion;

@Getter
@Setter
@NoArgsConstructor
public class ContentEmotionResultDto {
    private ChoiceEmotion choiceEmotion;

    @Builder
    public ContentEmotionResultDto(ChoiceEmotion choiceEmotion) {
        this.choiceEmotion = choiceEmotion;
    }
}
