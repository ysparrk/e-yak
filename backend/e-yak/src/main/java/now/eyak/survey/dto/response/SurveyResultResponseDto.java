package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SurveyResultResponseDto {
    private List<SurveyContentResultResponseDto> surveyContentResultResponses;

    @QueryProjection
    public SurveyResultResponseDto(List<SurveyContentResultResponseDto> surveyContentResultResponses) {
        this.surveyContentResultResponses = surveyContentResultResponses;
    }
}
