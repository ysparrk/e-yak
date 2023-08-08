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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
                .iotLocation(prescription.getIotLocation())
                .medicineDose(prescription.getMedicineDose())
                .unit(prescription.getUnit())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

}
