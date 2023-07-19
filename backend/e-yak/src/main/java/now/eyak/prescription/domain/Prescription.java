package now.eyak.prescription.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.domain.MedicineRoutine;
import now.eyak.member.domain.Member;
import org.hibernate.annotations.CreationTimestamp;

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
    @OneToMany
    private List<MedicineRoutine> medicineRoutines = new ArrayList<>();
    @ManyToOne
    private Member member;
    @CreationTimestamp
    private Timestamp createdAt;
}
