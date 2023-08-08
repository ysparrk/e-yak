package now.eyak.routine.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MedicineRoutineCheckDateQueryDto {
    private Long id;
    private LocalDate date;
    private Routine routine;
    private Boolean took;
    private Long memberId;
    private Long prescriptionId;

    @QueryProjection
    public MedicineRoutineCheckDateQueryDto(Long prescriptionId, Routine routine, Boolean took) {
        this.prescriptionId = prescriptionId;
        this.routine = routine;
        this.took = took;
    }

    @Builder
    public MedicineRoutineCheckDateQueryDto(Long id, LocalDate date, Routine routine, Boolean took, Long memberId, Long prescriptionId) {
        this.id = id;
        this.date = date;
        this.routine = routine;
        this.took = took;
        this.memberId = memberId;
        this.prescriptionId = prescriptionId;
    }
}
