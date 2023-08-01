package now.eyak.survey.service;

import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.dto.ContentEmotionResultDto;

public interface ContentEmotionResultService {
    ContentEmotionResult saveEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long memberId); // 저장
    ContentEmotionResult updateEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long memberId);  // 수정
    void deleteEmotionSurveyResult(Long contextEmotionResultId, Long memberId); // 삭제
}
