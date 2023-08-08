package now.eyak.routine.dto.response;

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
public class MedicineRoutineDateDto {
    private LocalDate date;
    private Long prescriptionId;
    private String customName;
    private Routine routine;
    private Boolean took;


    @Builder
    public MedicineRoutineDateDto(LocalDate date, Long prescriptionId, String customName, Routine routine, Boolean took) {
        this.date = date;
        this.prescriptionId = prescriptionId;
        this.customName = customName;
        this.routine = routine;
        this.took = took;
    }
}
