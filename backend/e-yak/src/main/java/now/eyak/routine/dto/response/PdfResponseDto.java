package now.eyak.routine.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import now.eyak.prescription.dto.query.PrescriptionListQueryDto;
import now.eyak.survey.dto.response.SurveyContentPdfResponseDto;

import java.util.List;

@Getter
@Setter
@ToString
public class PdfResponseDto {
    private List<PrescriptionListQueryDto> prescriptionList; // 기간에 복용중인 약들
    private List<SurveyContentPdfResponseDto> surveyContentList; // 기간의 건강설문

    @Builder
    public PdfResponseDto(List<PrescriptionListQueryDto> prescriptionList, List<SurveyContentPdfResponseDto> surveyContentList) {
        this.prescriptionList = prescriptionList;
        this.surveyContentList = surveyContentList;
    }
}
