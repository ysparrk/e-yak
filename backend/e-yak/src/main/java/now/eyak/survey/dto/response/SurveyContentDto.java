package now.eyak.survey.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SurveyContentDto {
    private ContentEmotionResultResponseDto contentEmotionResultResponse;
    private ContentStatusResultResponseDto contentStatusResultResponses;
    private ContentTextResultResponseDto contentTextResultResponse;

    @Builder
    public SurveyContentDto(ContentEmotionResultResponseDto contentEmotionResultResponse, ContentStatusResultResponseDto contentStatusResultResponses, ContentTextResultResponseDto contentTextResultResponse) {
        this.contentEmotionResultResponse = contentEmotionResultResponse;
        this.contentStatusResultResponses = contentStatusResultResponses;
        this.contentTextResultResponse = contentTextResultResponse;
    }
}
