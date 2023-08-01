package now.eyak.survey.service;

import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.dto.ContentStatusResultDto;

public interface ContentStatusResultService {
    ContentStatusResult saveStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long memberId); // 저장
}
