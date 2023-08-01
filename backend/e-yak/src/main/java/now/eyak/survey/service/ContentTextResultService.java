package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.dto.ContentTextResultUpdateDto;


public interface ContentTextResultService {
    ContentTextResult saveTextSurveyResult(ContentTextResultDto contentTextResultDto, Long memberId); // 저장
    ContentTextResult updateTextSurveyResult(ContentTextResultUpdateDto contentTextResultUpdateDto, Long memberId);  // 수정
    void deleteTextSurveyResult(Long contextTextResultId, Long memberId); // 삭제
}
