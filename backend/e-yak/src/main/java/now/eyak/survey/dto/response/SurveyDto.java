package now.eyak.survey.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SurveyDto {
    private Long id;
    private List<SurveyContentDto> surveyContentDtos;

    @QueryProjection
    public SurveyDto(Long id, List<SurveyContentDto> surveyContentDtos) {
        this.id = id;
        this.surveyContentDtos = surveyContentDtos;
    }
}
