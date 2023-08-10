package now.eyak.survey.service;

import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.request.ContentStatusResultUpdateDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;

import java.time.LocalDate;

public interface ContentStatusResultService {
    ContentStatusResult saveStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long surveyContentId, Long memberId); // 저장 바보
    ContentStatusResult updateStatusSurveyResult(ContentStatusResultUpdateDto contentStatusResultUpdateDto, Long surveyContentId, Long memberId); // 수정
    void deleteStatusSurveyResult(Long contentStatusResultId, Long memberId); // 삭제

    ContentStatusResultResponseDto getStatusResultByDateAndMember(LocalDate date, Long memberId); // 조회

}
