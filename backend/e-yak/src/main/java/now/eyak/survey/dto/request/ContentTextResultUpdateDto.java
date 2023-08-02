package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.ContentTextResult;

@Getter
@Setter
@Builder
public class ContentTextResultUpdateDto {
    private Long id;
    private String text;
    private Long surveyContentId;  // surveycontent의 id

    public ContentTextResult update(ContentTextResult contentTextResult) {
        contentTextResult.setText(text);

        return contentTextResult;
    }
}
