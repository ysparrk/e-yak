package now.eyak.prescription.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.domain.Prescription;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class PrescriptionDto {
    private String customName;
    private String icd;
    private String krName;
    private String engName;
    // TODO: 커스텀 아이콘 정보 추가
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer iotLocation; // 약통 칸 번호
    private Integer medicineDose; // 1회 투여 개수
    private String unit; // 투여 단위
    private List<Routine> routines;

    public Prescription toEntity() {
        return Prescription.builder()
                .customName(customName)
                .icd(icd)
                .krName(krName)
                .engName(engName)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .iotLocation(iotLocation)
                .medicineDose(medicineDose)
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
        prescription.setUnit(unit);
    }

}
