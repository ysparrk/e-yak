package now.eyak.routine.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.enumeration.Routine;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
public class MedicineRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true, insertable = false, updatable = false)
    private Routine routine;

    public MedicineRoutine(Long id, Routine routine) {
        this.id = id;
        this.routine = routine;
    }
}
