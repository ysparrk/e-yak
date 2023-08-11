package now.eyak.prescription.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class PrescriptionListQueryDto {
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

    @QueryProjection
    public PrescriptionListQueryDto(String icd, String customName, String krName, String engName, LocalDateTime startDateTime, LocalDateTime endDateTime, List<Routine> medicineRoutines, Integer iotLocation, Float medicineDose, Integer medicineShape, String unit) {
        this.icd = icd;
        this.customName = customName;
        this.krName = krName;
        this.engName = engName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.medicineRoutines = medicineRoutines;
        this.iotLocation = iotLocation;
        this.medicineDose = medicineDose;
        this.medicineShape = medicineShape;
        this.unit = unit;
    }
}
