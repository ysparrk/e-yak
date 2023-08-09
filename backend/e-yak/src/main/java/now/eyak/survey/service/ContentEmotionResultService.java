package now.eyak.survey.service;

import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentEmotionResultUpdateDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ContentEmotionResultService {
    ContentEmotionResult saveEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long surveyContentId, Long memberId); // 저장
    public ContentEmotionResult updateEmotionSurveyResult(ContentEmotionResultUpdateDto contentEmotionResultUpdateDto, Long surveyContentId, Long memberId);  // 수정
    void deleteEmotionSurveyResult(Long contentEmotionResultId, Long memberId); // 삭제

    List<ContentEmotionResultResponseDto> getEmotionResultsByDateAndMember(LocalDate date, Long memberId); // 조회
}
