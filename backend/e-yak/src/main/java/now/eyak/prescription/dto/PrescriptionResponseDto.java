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
    private List<BedAfterQueryDto> bedAfterQueryResponses;
    private List<BreakfastBeforeQueryDto> breakfastBeforeQueryResponses;
    private List<BreakfastAfterQueryDto> breakfastAfterQueryResponses;
    private List<LunchBeforeQueryDto> lunchBeforeQueryResponses;
    private List<LunchAfterQueryDto> lunchAfterQueryResponses;
    private List<DinnerBeforeQueryDto> dinnerBeforeQueryResponses;
    private List<DinnerAfterQueryDto> dinnerAfterQueryResponses;
    private List<BedBeforeQueryDto> bedBeforeQueryResponses;

    @Builder
    public PrescriptionResponseDto(List<BedAfterQueryDto> bedAfterQueryResponses, List<BreakfastBeforeQueryDto> breakfastBeforeQueryResponses, List<BreakfastAfterQueryDto> breakfastAfterQueryResponses, List<LunchBeforeQueryDto> lunchBeforeQueryResponses, List<LunchAfterQueryDto> lunchAfterQueryResponses, List<DinnerBeforeQueryDto> dinnerBeforeQueryResponses, List<DinnerAfterQueryDto> dinnerAfterQueryResponses, List<BedBeforeQueryDto> bedBeforeQueryResponses) {
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
