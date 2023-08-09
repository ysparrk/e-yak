package now.eyak.survey.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.enumeration.SurveyContentType;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class SurveyContentIdResponseDto {
    private Long surveyContentId;
    private SurveyContentType surveyContentType;

    public SurveyContentIdResponseDto(Long surveyContentId, SurveyContentType surveyContentType) {
        this.surveyContentId = surveyContentId;
        this.surveyContentType = surveyContentType;
    }

    public static SurveyContentIdResponseDto from(SurveyContent surveyContent) {
        return SurveyContentIdResponseDto.builder()
                .surveyContentId(surveyContent.getId())
                .surveyContentType(surveyContent.getSurveyContentType())
                .build();
    }
}
