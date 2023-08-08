package now.eyak.prescription.dto;

import lombok.*;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.enumeration.Routine;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
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
