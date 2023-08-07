package now.eyak.prescription.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.domain.Prescription;
import now.eyak.routine.enumeration.Routine;

@Getter
@Setter
@Builder
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PrescriptionDto {
    private String icd;
    private String customName;
    private String krName;
    private String engName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<Routine> medicineRoutines;
    private Integer iotLocation; // 약통 칸 번호
    private Float medicineDose; // 1회 투여 개수
    private Integer medicineShape; // 이모지 번호
    private String unit; // 투여 단위
    private List<Routine> medicineRoutines;

    public Prescription toEntity() {
        return Prescription.builder()
                .icd(icd)
                .customName(customName)
                .krName(krName)
                .engName(engName)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .iotLocation(iotLocation)
                .medicineDose(medicineDose)
                .medicineShape(medicineShape)
                .unit(unit)
                .build();
    }

    public void update(Prescription prescription) {
        prescription.setCustomName(customName);
        prescription.setIcd(icd);
        prescription.setKrName(krName);
        prescription.setEngName(engName);
        prescription.setStartDateTime(startDateTime);
        prescription.setEndDateTime(endDateTime);
        prescription.setIotLocation(iotLocation);
        prescription.setMedicineDose(medicineDose);
        prescription.setMedicineShape(medicineShape);
        prescription.setUnit(unit);
    }

}
