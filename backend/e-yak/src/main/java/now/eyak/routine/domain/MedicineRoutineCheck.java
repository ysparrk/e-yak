package now.eyak.routine.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MedicineRoutineCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    @ManyToOne
    private MedicineRoutine medicineRoutine;
    private Boolean took;  // 약 복용 확인
    @ManyToOne
    private Member member;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prescription prescription;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public MedicineRoutineCheck(LocalDate date, MedicineRoutine medicineRoutine, Boolean took, Member member, Prescription prescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.date = date;
        this.medicineRoutine = medicineRoutine;
        this.took = took;
        this.member = member;
        this.prescription = prescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
