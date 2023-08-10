package now.eyak.survey.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.survey.enumeration.ChoiceStatus;

import java.util.List;

@Getter
@Setter
@Builder
public class ContentStatusResultDto {
    private List<ChoiceStatus> selectedStatusChoices;
}