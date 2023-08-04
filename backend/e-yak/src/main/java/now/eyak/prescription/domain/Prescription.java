package now.eyak.prescription.domain;

import jakarta.persistence.*;
import lombok.*;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.member.domain.Member;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    // TODO: 커스텀 아이콘 정보 추가
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer iotLocation; // 약통 칸 번호
    private Integer medicineDose; // 1회 투여 개수
    private String unit; // 투여 단위
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines = new ArrayList<>();
    @ManyToOne
    private Member member;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public Prescription(String customName, String icd, String krName, String engName, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer iotLocation, Integer medicineDose, String unit, List<MedicineRoutine> medicineRoutines, Member member) {
        this.customName = customName;
        this.icd = icd;
        this.krName = krName;
        this.engName = engName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.iotLocation = iotLocation;
        this.medicineDose = medicineDose;
        this.unit = unit;
        this.member = member;
    }
    public void add(PrescriptionMedicineRoutine prescriptionMedicineRoutine) {
        prescriptionMedicineRoutines.add(prescriptionMedicineRoutine);
        prescriptionMedicineRoutine.setPrescription(this);
    }
}
