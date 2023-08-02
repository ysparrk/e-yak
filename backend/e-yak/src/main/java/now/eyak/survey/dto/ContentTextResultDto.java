package now.eyak.survey.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContentTextResultDto {
    private String text;
    private Long surveyContentId;  // surveycontent의 id
}
