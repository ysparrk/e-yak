package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class SurveyContentResultResponseDto {
    private List<ContentEmotionResultResponseDto> contentEmotionResultResponses;
    private List<ContentStatusResultResponseDto> contentStatusResultResponses;
    private ContentTextResultResponseDto contentTextResultResponse;

    @QueryProjection
    public SurveyContentResultResponseDto(List<ContentEmotionResultResponseDto> contentEmotionResultResponses, List<ContentStatusResultResponseDto> contentStatusResultResponses, ContentTextResultResponseDto contentTextResultResponse) {
        this.contentEmotionResultResponses = contentEmotionResultResponses;
        this.contentStatusResultResponses = contentStatusResultResponses;
        this.contentTextResultResponse = contentTextResultResponse;
    }
}
