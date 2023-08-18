package now.eyak.routine.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class MedicineRoutineMonthResponseDto {

    private List<MedicineRoutineMonthDateDto> dates;

    @Builder
    public MedicineRoutineMonthResponseDto(List<MedicineRoutineMonthDateDto> dates) {
        this.dates = dates;
    }
}
