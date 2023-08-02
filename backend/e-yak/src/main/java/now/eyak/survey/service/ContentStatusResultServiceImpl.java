package now.eyak.survey.service;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentStatusResultDto;
import now.eyak.survey.repository.ContentStatusResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentStatusResultServiceImpl implements ContentStatusResultService {

    private final ContentStatusResultRepository contentStatusResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;

    /**
     * Status 설문 응답 저장
     *
     * @param contentStatusResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentStatusResult saveStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(contentStatusResultDto.getSurveyContentId()).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentStatusResult contentStatusResult = ContentStatusResult.builder()
                .surveyContent(surveyContent)
                .selectedStatusChoices(contentStatusResultDto.getSelectedStatusChoices())
                .member(member)
                .build();

        return contentStatusResultRepository.save(contentStatusResult);
    }

    /**
     * Status 설문 응답 수정
     *
     * @param contentStatusResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentStatusResult updateStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(contentStatusResultDto.getSurveyContentId()).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentStatusResult contentStatusResult = contentStatusResultRepository.findByIdAndMember(contentStatusResultDto.getId(), member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당하는 ContentStatusResult가 존재하지 않습니다."));

        contentStatusResult.setSelectedStatusChoices(new ArrayList<>(contentStatusResultDto.getSelectedStatusChoices()));
        return contentStatusResultRepository.save(contentStatusResult);

    }

    /**
     * Status 설문 응답 삭제
     *
     * @param contentStatusResultId
     * @param memberId
     */
    @Transactional
    @Override
    public void deleteStatusSurveyResult(Long contentStatusResultId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        contentStatusResultRepository.deleteById(contentStatusResultId);

    }
}