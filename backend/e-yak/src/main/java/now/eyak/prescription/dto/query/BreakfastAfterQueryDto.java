package now.eyak.prescription.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BreakfastAfterQueryDto {
    private Long id;
    private String customName;
    private Integer iotLocation;
    private Integer medicineShape;
    private Boolean took;

    @QueryProjection
    public BreakfastAfterQueryDto(Long id, String customName, Integer iotLocation, Integer medicineShape, Boolean took) {
        this.id = id;
        this.customName = customName;
        this.iotLocation = iotLocation;
        this.medicineShape = medicineShape;
        this.took = took;
    }
}
