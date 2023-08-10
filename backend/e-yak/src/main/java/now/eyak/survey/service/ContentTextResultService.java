package now.eyak.survey.service;

import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.dto.request.ContentTextResultUpdateDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;

import java.time.LocalDate;


public interface ContentTextResultService {
    ContentTextResult saveTextSurveyResult(ContentTextResultDto contentTextResultDto, Long surveyContentId, Long memberId); // 저장
    ContentTextResult updateTextSurveyResult(ContentTextResultUpdateDto contentTextResultUpdateDto, Long surveyContentId, Long memberId);  // 수정
    void deleteTextSurveyResult(Long contentTextResultId, Long memberId); // 삭제

    ContentTextResultResponseDto getTextResultByDateAndMember(LocalDate date, Long memberId); // 조회

}
