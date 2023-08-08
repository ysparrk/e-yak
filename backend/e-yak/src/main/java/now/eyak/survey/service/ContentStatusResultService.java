package now.eyak.survey.service;

import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ContentStatusResultService {
    ContentStatusResult saveStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long memberId); // 저장
    ContentStatusResult updateStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long memberId); // 수정
    void deleteStatusSurveyResult(Long contentStatusResultId, Long memberId); // 삭제

    List<ContentStatusResultResponseDto> getStatusResultsByDateAndMember(LocalDate date, Long memberId); // 조회

}
