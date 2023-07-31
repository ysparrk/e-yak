package now.eyak.survey.service;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.repository.ContentTextResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentTextResultServiceImpl implements ContentTextResultService {

    private final ContentTextResultRepository contentTextResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public ContentTextResult saveTextSurveyResult(ContentTextResultDto contentTextResultDto, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(contentTextResultDto.getSurveyContentId()).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentTextResult contentTextResult = ContentTextResult.builder()
                .text(contentTextResultDto.getText())
                .surveyContent(surveyContent)
                .member(member)
                .build();

        return contentTextResultRepository.save(contentTextResult);
    }

}
