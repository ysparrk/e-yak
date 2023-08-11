package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.survey.enumeration.ChoiceStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContentStatusResultDto {
    private List<ChoiceStatus> selectedStatusChoices;

    @Builder
    public ContentStatusResultDto(List<ChoiceStatus> selectedStatusChoices) {
        this.selectedStatusChoices = selectedStatusChoices;
    }
}
