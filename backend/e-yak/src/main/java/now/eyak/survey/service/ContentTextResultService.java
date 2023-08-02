package now.eyak.survey.service;

import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.dto.request.ContentTextResultUpdateDto;


public interface ContentTextResultService {
    ContentTextResult saveTextSurveyResult(ContentTextResultDto contentTextResultDto, Long memberId); // 저장
    ContentTextResult updateTextSurveyResult(ContentTextResultUpdateDto contentTextResultUpdateDto, Long memberId);  // 수정
    void deleteTextSurveyResult(Long contextTextResultId, Long memberId); // 삭제
}
