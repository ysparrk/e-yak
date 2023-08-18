package now.eyak.routine.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MedicineRoutineCheckIdResponseDto {
    private Long id;
    private Boolean took;

    @QueryProjection
    public MedicineRoutineCheckIdResponseDto(Long id, Boolean took) {
        this.id = id;
        this.took = took;
    }
}
