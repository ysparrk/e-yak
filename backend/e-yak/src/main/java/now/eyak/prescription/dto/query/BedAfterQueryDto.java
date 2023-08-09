package now.eyak.prescription.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BedAfterQueryDto {
    private Long id;
    private String customName;
    private Integer iotLocation; // 약통 칸 번호
    private Integer medicineShape; // 이모지 번호

    @QueryProjection
    public BedAfterQueryDto(Long id, String customName, Integer iotLocation, Integer medicineShape) {
        this.id = id;
        this.customName = customName;
        this.iotLocation = iotLocation;
        this.medicineShape = medicineShape;
    }
}
