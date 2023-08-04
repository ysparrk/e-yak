package now.eyak.prescription.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String efficasy; // 효능 efcyQesitm
    private String useMethod; // 용법 useMethodQesitm
    private String warnInfo; // 주의사항경고 atpnWarnQesitm
    private String warn; // 주의사항 atpnQesitm
    private String warnIntrc; // 상호작용 intrcQesitm
    private String sideEffect; // 부작용 seQesitm
}
