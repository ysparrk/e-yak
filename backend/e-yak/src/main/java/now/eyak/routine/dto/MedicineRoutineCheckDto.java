package now.eyak.routine.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
public class MedicineRoutineCheckDto {
    private Long id;
    private LocalDate date;
    private Routine routine;
    private Boolean took;
    private Long memberId;
    private Long prescriptionId;

    @QueryProjection
    public MedicineRoutineCheckDto(Long id, LocalDate date, Routine routine, Boolean took, Long memberId, Long prescriptionId) {
        this.id = id;
        this.date = date;
        this.routine = routine;
        this.took = took;
        this.memberId = memberId;
        this.prescriptionId = prescriptionId;
    }


    public static boolean isTook(MedicineRoutineCheckDto medicineRoutineCheckDto) {
        return medicineRoutineCheckDto.getTook();
    }

}
