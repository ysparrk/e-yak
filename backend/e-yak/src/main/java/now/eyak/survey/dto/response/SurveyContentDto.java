package now.eyak.survey.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class SurveyContentDto {
    private List<ContentEmotionResultResponseDto> contentEmotionResultResponses;
    private List<ContentStatusResultResponseDto> contentStatusResultResponses;
    private List<ContentTextResultResponseDto> contentTextResultResponse;

    @Builder
    public SurveyContentDto(List<ContentEmotionResultResponseDto> contentEmotionResultResponses, List<ContentStatusResultResponseDto> contentStatusResultResponses, List<ContentTextResultResponseDto> contentTextResultResponse) {
        this.contentEmotionResultResponses = contentEmotionResultResponses;
        this.contentStatusResultResponses = contentStatusResultResponses;
        this.contentTextResultResponse = contentTextResultResponse;
    }
}
