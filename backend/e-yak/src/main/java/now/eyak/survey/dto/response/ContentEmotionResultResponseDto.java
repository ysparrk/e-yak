package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.survey.enumeration.ChoiceEmotion;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ContentEmotionResultResponseDto {
    private Long memberId;
    private ChoiceEmotion choiceEmotion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public ContentEmotionResultResponseDto(Long memberId, ChoiceEmotion choiceEmotion, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.choiceEmotion = choiceEmotion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
