package now.eyak.prescription.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.domain.Prescription;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class PrescriptionResponseDto {
    private Long id;
    private String icd;
    private String customName;
    private String krName;
    private String engName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<Routine> medicineRoutines;
    private Integer iotLocation; // 약통 칸 번호
    private Integer medicineShape; // 이모지 번호
    private Float medicineDose; // 1회 투여 개수
    private String unit; // 투여 단위
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PrescriptionResponseDto from(Prescription prescription) {
        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .customName(prescription.getCustomName())
                .icd(prescription.getIcd())
                .krName(prescription.getKrName())
                .engName(prescription.getEngName())
                .startDateTime(prescription.getStartDateTime())
                .endDateTime(prescription.getEndDateTime())
                .medicineRoutines(prescription.getPrescriptionMedicineRoutines().stream().map(prescriptionMedicineRoutine -> prescriptionMedicineRoutine.getMedicineRoutine().getRoutine()).toList())
                .iotLocation(prescription.getIotLocation())
                .medicineShape(prescription.getMedicineShape())
                .medicineDose(prescription.getMedicineDose())
                .unit(prescription.getUnit())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

}
