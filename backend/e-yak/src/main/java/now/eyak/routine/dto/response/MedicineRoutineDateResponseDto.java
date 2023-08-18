package now.eyak.routine.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.survey.dto.response.SurveyContentDto;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class MedicineRoutineDateResponseDto {
    private LocalDate date;
    private List<MedicineRoutineDateDto> medicineRoutineDateDtos;
    private SurveyContentDto surveyContentDtos;

    @Builder
    public MedicineRoutineDateResponseDto(LocalDate date, List<MedicineRoutineDateDto> medicineRoutineDateDtos, SurveyContentDto surveyContentDtos) {
        this.date = date;
        this.medicineRoutineDateDtos = medicineRoutineDateDtos;
        this.surveyContentDtos = surveyContentDtos;
    }
}
