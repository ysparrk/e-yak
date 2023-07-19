package now.eyak.routine.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.routine.enumeration.Routine;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MedicineRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Routine routine;
}
