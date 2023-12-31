package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.survey.domain.ContentTextResult;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ContentTextResultResponseDto {
    private Long contentTextResultId;
    private Long memberId;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public ContentTextResultResponseDto(Long contentTextResultId, Long memberId, String text, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.contentTextResultId = contentTextResultId;
        this.memberId = memberId;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ContentTextResultResponseDto of(ContentTextResult contentTextResult) {
        return ContentTextResultResponseDto.builder()
                .contentTextResultId(contentTextResult == null ? -1 : contentTextResult.getId())
                .memberId(contentTextResult == null ? -1 : contentTextResult.getMember().getId())
                .text(contentTextResult == null ? "" : contentTextResult.getText())
                .createdAt(contentTextResult == null ? LocalDateTime.MIN : contentTextResult.getCreatedAt())
                .updatedAt(contentTextResult == null ? LocalDateTime.MIN : contentTextResult.getUpdatedAt())
                .build();
    }
}
