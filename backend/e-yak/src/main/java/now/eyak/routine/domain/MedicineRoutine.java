package now.eyak.routine.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.enumeration.Routine;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MedicineRoutine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private Routine routine;

    @Builder
    public MedicineRoutine(Long id, Routine routine) {
        this.id = id;
        this.routine = routine;
    }
}
