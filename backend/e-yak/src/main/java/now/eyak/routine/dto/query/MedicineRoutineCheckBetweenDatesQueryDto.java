package now.eyak.routine.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MedicineRoutineCheckBetweenDatesQueryDto {
    private Long id;
    private LocalDate date;
    private Routine routine;
    private Boolean took;
    private Long memberId;
    private Long prescriptionId;

    @QueryProjection
    public MedicineRoutineCheckBetweenDatesQueryDto(Boolean took) {
        this.took = took;
    }

    public static boolean isTook(MedicineRoutineCheckBetweenDatesQueryDto medicineRoutineCheckBetweenDatesQueryDto) {
        return medicineRoutineCheckBetweenDatesQueryDto.getTook();
    }
}
