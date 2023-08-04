package now.eyak.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.enumeration.Routine;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MedicineRoutineResponseDto {
    private Long id;
    private Routine routine;

    public MedicineRoutineResponseDto(Long id, Routine routine) {
        this.id = id;
        this.routine = routine;
    }

    public static MedicineRoutineResponseDto from(PrescriptionMedicineRoutine prescriptionMedicineRoutine) {
        return MedicineRoutineResponseDto.builder()
                .id(prescriptionMedicineRoutine.getId())
                .routine(prescriptionMedicineRoutine.getMedicineRoutine().getRoutine())
                .build();
    }
}
