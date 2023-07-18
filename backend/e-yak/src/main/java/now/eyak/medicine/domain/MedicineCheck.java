package now.eyak.medicine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.DailyRoutine;
import now.eyak.prescription.domain.Prescription;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MedicineCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @OneToOne
    private DailyRoutine routine;
    @ManyToOne
    private Member member;
    @ManyToOne
    private Prescription prescription;
    @CreationTimestamp
    private Timestamp checkedAt;
}
