package now.eyak.prescription.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import now.eyak.routine.domain.MedicineRoutine;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
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
    private List<MedicineRoutine> medicineRoutines;
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
                .medicineRoutines(prescription.getMedicineRoutines())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

}
