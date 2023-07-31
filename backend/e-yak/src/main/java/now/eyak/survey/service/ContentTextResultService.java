package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.ContentTextResultDto;


public interface ContentTextResultService {
    ContentTextResult saveTextSurveyResult(ContentTextResultDto contentTextResultDto, Long memberId); // 저장

}
