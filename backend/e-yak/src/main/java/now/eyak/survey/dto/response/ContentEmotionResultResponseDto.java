package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.enumeration.ChoiceEmotion;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ContentEmotionResultResponseDto {
    private Long contentEmotionResultId;
    private Long memberId;
    private ChoiceEmotion choiceEmotion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public ContentEmotionResultResponseDto(Long contentEmotionResultId, Long memberId, ChoiceEmotion choiceEmotion, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.contentEmotionResultId = contentEmotionResultId;
        this.memberId = memberId;
        this.choiceEmotion = choiceEmotion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ContentEmotionResultResponseDto of(ContentEmotionResult contentEmotionResult) {
        return ContentEmotionResultResponseDto.builder()
                .contentEmotionResultId(contentEmotionResult == null ? -1 : contentEmotionResult.getId())
                .memberId(contentEmotionResult == null ? -1 : contentEmotionResult.getMember().getId())
                .choiceEmotion(contentEmotionResult == null ? ChoiceEmotion.SOSO : contentEmotionResult.getChoiceEmotion())
                .createdAt(contentEmotionResult == null ? LocalDateTime.MIN : contentEmotionResult.getCreatedAt())
                .updatedAt(contentEmotionResult == null ? LocalDateTime.MIN : contentEmotionResult.getUpdatedAt())
                .build();
    }
}
