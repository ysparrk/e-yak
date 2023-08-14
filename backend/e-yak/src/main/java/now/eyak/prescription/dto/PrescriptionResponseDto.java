package now.eyak.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.dto.query.*;

import java.util.List;

@Getter
@Setter
@ToString
public class PrescriptionResponseDto {
    // 8개의 루틴에 대해 그 루틴에 따라 복용해야 하는 처방전의 id를 넣는다
    private List<PrescriptionRoutineQueryDto> bedAfterQueryResponses;
    private List<PrescriptionRoutineQueryDto> breakfastBeforeQueryResponses;
    private List<PrescriptionRoutineQueryDto> breakfastAfterQueryResponses;
    private List<PrescriptionRoutineQueryDto> lunchBeforeQueryResponses;
    private List<PrescriptionRoutineQueryDto> lunchAfterQueryResponses;
    private List<PrescriptionRoutineQueryDto> dinnerBeforeQueryResponses;
    private List<PrescriptionRoutineQueryDto> dinnerAfterQueryResponses;
    private List<PrescriptionRoutineQueryDto> bedBeforeQueryResponses;

    @Builder
    public PrescriptionResponseDto(List<PrescriptionRoutineQueryDto> bedAfterQueryResponses, List<PrescriptionRoutineQueryDto> breakfastBeforeQueryResponses, List<PrescriptionRoutineQueryDto> breakfastAfterQueryResponses, List<PrescriptionRoutineQueryDto> lunchBeforeQueryResponses, List<PrescriptionRoutineQueryDto> lunchAfterQueryResponses, List<PrescriptionRoutineQueryDto> dinnerBeforeQueryResponses, List<PrescriptionRoutineQueryDto> dinnerAfterQueryResponses, List<PrescriptionRoutineQueryDto> bedBeforeQueryResponses) {
        this.bedAfterQueryResponses = bedAfterQueryResponses;
        this.breakfastBeforeQueryResponses = breakfastBeforeQueryResponses;
        this.breakfastAfterQueryResponses = breakfastAfterQueryResponses;
        this.lunchBeforeQueryResponses = lunchBeforeQueryResponses;
        this.lunchAfterQueryResponses = lunchAfterQueryResponses;
        this.dinnerBeforeQueryResponses = dinnerBeforeQueryResponses;
        this.dinnerAfterQueryResponses = dinnerAfterQueryResponses;
        this.bedBeforeQueryResponses = bedBeforeQueryResponses;
    }
}
