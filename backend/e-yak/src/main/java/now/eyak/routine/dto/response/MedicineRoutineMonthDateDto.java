package now.eyak.routine.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
public class MedicineRoutineMonthDateDto {
    private LocalDate date;
    private Long fullDose;
    private Long actualDose;

    @Builder
    public MedicineRoutineMonthDateDto(LocalDate date, Long fullDose, Long actualDose) {
        this.date = date;
        this.fullDose = fullDose;
        this.actualDose = actualDose;
    }
}
