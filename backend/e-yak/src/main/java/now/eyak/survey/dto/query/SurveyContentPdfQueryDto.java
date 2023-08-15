package now.eyak.survey.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.ContentTextResult;

import java.time.LocalDate;

@Getter
@Setter
public class SurveyContentPdfQueryDto {
    private LocalDate date;
    private ContentEmotionResult contentEmotionResult;
    private ContentStatusResult contentStatusResult;
    private ContentTextResult contentTextResult;

    @QueryProjection
    public SurveyContentPdfQueryDto(LocalDate date, ContentEmotionResult contentEmotionResult, ContentStatusResult contentStatusResult, ContentTextResult contentTextResult) {
        this.date = date;
        this.contentEmotionResult = contentEmotionResult;
        this.contentStatusResult = contentStatusResult;
        this.contentTextResult = contentTextResult;
    }
}
