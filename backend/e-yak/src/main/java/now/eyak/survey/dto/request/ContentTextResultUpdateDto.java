package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.domain.ContentTextResult;

@Getter
@Setter
@Builder
public class ContentTextResultUpdateDto {
    private Long contentTextResultId;
    private String text;

    public void update(ContentTextResult contentTextResult) {
        contentTextResult.setText(text);
    }
}
