package now.eyak.survey.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.survey.domain.SurveyContent;

@Getter
@Setter
@NoArgsConstructor
public class SurveyContentIdResponseDto {
    private Long id;

    @Builder
    public SurveyContentIdResponseDto(Long id) {
        this.id = id;
    }

    public static SurveyContentIdResponseDto from(SurveyContent surveyContent) {
        return SurveyContentIdResponseDto.builder()
                .id(surveyContent.getId())
                .build();
    }
}
