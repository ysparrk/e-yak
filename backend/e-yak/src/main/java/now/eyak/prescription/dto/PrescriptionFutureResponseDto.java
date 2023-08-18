package now.eyak.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.dto.query.PrescriptionRoutineFutureQueryDto;

import java.util.List;

@Getter
@Setter
@ToString
public class PrescriptionFutureResponseDto {
    private List<PrescriptionRoutineFutureQueryDto> bedAfterQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> breakfastBeforeQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> breakfastAfterQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> lunchBeforeQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> lunchAfterQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> dinnerBeforeQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> dinnerAfterQueryResponses;
    private List<PrescriptionRoutineFutureQueryDto> bedBeforeQueryResponses;

    @Builder
    public PrescriptionFutureResponseDto(List<PrescriptionRoutineFutureQueryDto> bedAfterQueryResponses, List<PrescriptionRoutineFutureQueryDto> breakfastBeforeQueryResponses, List<PrescriptionRoutineFutureQueryDto> breakfastAfterQueryResponses, List<PrescriptionRoutineFutureQueryDto> lunchBeforeQueryResponses, List<PrescriptionRoutineFutureQueryDto> lunchAfterQueryResponses, List<PrescriptionRoutineFutureQueryDto> dinnerBeforeQueryResponses, List<PrescriptionRoutineFutureQueryDto> dinnerAfterQueryResponses, List<PrescriptionRoutineFutureQueryDto> bedBeforeQueryResponses) {
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
