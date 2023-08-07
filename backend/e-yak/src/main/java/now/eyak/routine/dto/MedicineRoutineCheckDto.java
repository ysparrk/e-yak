package now.eyak.routine.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MedicineRoutineCheckDto {
    private Long id;
    private LocalDate date;
    private Routine routine;
    private Boolean took;
    private Long memberId;
    private Long prescriptionId;

    @QueryProjection
    public MedicineRoutineCheckDto(LocalDate date, Boolean took) {
        this.date = date;
        this.took = took;
    }


    public static boolean isTook(MedicineRoutineCheckDto medicineRoutineCheckDto) {
        return medicineRoutineCheckDto.getTook();
    }

}
