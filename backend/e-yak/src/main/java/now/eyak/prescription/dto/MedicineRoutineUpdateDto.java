package now.eyak.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.enumeration.Routine;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MedicineRoutineUpdateDto {
    private List<Routine> routines;

    @Builder
    public MedicineRoutineUpdateDto(List<Routine> routines) {
        this.routines = routines;
    }
}
