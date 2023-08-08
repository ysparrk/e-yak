package now.eyak.routine.dto.request;

import lombok.Builder;
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

    @Builder
    public MedicineRoutineCheckDto(Long id, LocalDate date, Routine routine, Boolean took, Long memberId, Long prescriptionId) {
        this.id = id;
        this.date = date;
        this.routine = routine;
        this.took = took;
        this.memberId = memberId;
        this.prescriptionId = prescriptionId;
    }
}
