package now.eyak.survey.service;

import now.eyak.member.domain.Member;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.repository.ContentTextResultRepository;
import org.springframework.stereotype.Service;

@Service
public class ContentTextResultServiceImpl implements ContentTextResultService {

    private final ContentTextResultRepository contentTextResultRepository;

    public ContentTextResultServiceImpl(ContentTextResultRepository contentTextResultRepository) {
        this.contentTextResultRepository = contentTextResultRepository;
    }

    @Override
    public Long postTextSurveyResult(Long surveyContentId, ContentTextResultDto contentTextResultDto, Member member) {
        ContentTextResult contentTextResult = new ContentTextResult();

        // SurveyContent 엔티티를 가져옵니다.
        SurveyContent surveyContent = new SurveyContent();
        surveyContent.setId(surveyContentId);

        contentTextResult.setSurveyContent(surveyContent);
        contentTextResult.setText(contentTextResultDto.getText());
        contentTextResult.setMember(member);

        ContentTextResult savedContentTextResult = contentTextResultRepository.save(contentTextResult);
        return savedContentTextResult.getId();
    }

}
