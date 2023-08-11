package now.eyak.prescription.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customName;
    private String icd;
    private String krName;
    private String engName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer iotLocation; // 약통 칸 번호
    private Integer medicineShape; // 이모지 번호
    private Float medicineDose; // 1회 투여 개수
    private String unit; // 투여 단위
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines = new ArrayList<>();
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private Member member;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    @UpdateTimestamp
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @Builder
    public Prescription(Long id, String customName, String icd, String krName, String engName, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer iotLocation, Integer medicineShape, Float medicineDose, String unit, Member member) {
        this.id = id;
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
        this.member = member;
    }

    @QueryProjection
    public Prescription(String customName, String icd, String krName, String engName, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer iotLocation, Integer medicineShape, Float medicineDose, String unit, List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines) {
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
    }

    public void add(PrescriptionMedicineRoutine prescriptionMedicineRoutine) {
        prescriptionMedicineRoutines.add(prescriptionMedicineRoutine);
        prescriptionMedicineRoutine.setPrescription(this);
    }
}
