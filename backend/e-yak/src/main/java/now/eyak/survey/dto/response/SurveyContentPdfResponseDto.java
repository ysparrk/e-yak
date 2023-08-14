package now.eyak.survey.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class SurveyContentPdfResponseDto {
    private LocalDate date;
    private ContentEmotionResultResponseDto contentEmotionResultResponse;
    private ContentStatusResultResponseDto contentStatusResultResponse;
    private ContentTextResultResponseDto contentTextResultResponse;


    @Builder
    public SurveyContentPdfResponseDto(LocalDate date, ContentEmotionResultResponseDto contentEmotionResultResponse, ContentStatusResultResponseDto contentStatusResultResponse, ContentTextResultResponseDto contentTextResultResponse) {
        this.date = date;
        this.contentEmotionResultResponse = contentEmotionResultResponse;
        this.contentStatusResultResponse = contentStatusResultResponse;
        this.contentTextResultResponse = contentTextResultResponse;
    }

}
