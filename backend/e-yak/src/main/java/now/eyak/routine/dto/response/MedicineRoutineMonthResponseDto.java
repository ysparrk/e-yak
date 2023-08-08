package now.eyak.routine.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class MedicineRoutineMonthResponseDto {

    private YearMonth yearMonth;
    private List<MedicineRoutineMonthDateDto> dates;

    @Builder
    public MedicineRoutineMonthResponseDto(YearMonth yearMonth, List<MedicineRoutineMonthDateDto> dates) {
        this.yearMonth = yearMonth;
        this.dates = dates;
    }
}
