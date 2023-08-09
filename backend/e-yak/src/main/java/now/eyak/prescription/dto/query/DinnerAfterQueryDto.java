package now.eyak.prescription.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DinnerAfterQueryDto {
    private Long id;
    private String customName;
    private Integer iotLocation;
    private Integer medicineShape;

    @QueryProjection
    public DinnerAfterQueryDto(Long id, String customName, Integer iotLocation, Integer medicineShape) {
        this.id = id;
        this.customName = customName;
        this.iotLocation = iotLocation;
        this.medicineShape = medicineShape;
    }
}
