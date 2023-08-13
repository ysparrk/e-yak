package now.eyak.prescription.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class PrescriptionListQueryDto {
    private String customName;
    private String icd;
    private String krName;
    private String engName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer iotLocation;
    private Integer medicineShape; // 이모지 번호
    private Float medicineDose; // 1회 투여 개수
    private String unit; // 투여 단위
    private List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines;
    private Long fullDose;
    private Long actualDose;


    @Builder
    public PrescriptionListQueryDto(String customName, String icd, String krName, String engName, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer iotLocation, Integer medicineShape, Float medicineDose, String unit, List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines, Long fullDose, Long actualDose) {
        this.customName = customName;
        this.icd = icd;
        this.krName = krName;
        this.engName = engName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.iotLocation = iotLocation;
        this.medicineShape = medicineShape;
        this.medicineDose = medicineDose;
        this.unit = unit;
        this.prescriptionMedicineRoutines = prescriptionMedicineRoutines;
        this.fullDose = fullDose;
        this.actualDose = actualDose;
    }
}
