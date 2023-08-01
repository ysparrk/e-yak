package now.eyak.survey.service;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.ContentEmotionResultDto;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.repository.ContentEmotionResultRepository;
import now.eyak.survey.repository.ContentTextResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentEmotionResultServiceImpl implements ContentEmotionResultService {

    private final ContentEmotionResultRepository contentEmotionResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;

    /**
     * Emotion 설문 응답 저장
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentEmotionResult saveEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(contentEmotionResultDto.getSurveyContentId()).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentEmotionResult contentEmotionResult = ContentEmotionResult.builder()
                .surveyContent(surveyContent)
                .choiceEmotion(contentEmotionResultDto.getChoiceEmotion())
                .member(member)
                .build();

        return contentEmotionResultRepository.save(contentEmotionResult);
    }

    /**
     * Emotion 설문 응답 수정
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentEmotionResult updateEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(contentEmotionResultDto.getSurveyContentId()).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentEmotionResult contentEmotionResult = contentEmotionResultRepository.findByIdAndMember(contentEmotionResultDto.getId(), member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당하는 ContentEmotionResult가 존재하지 않습니다."));

        contentEmotionResult = contentEmotionResultDto.update(contentEmotionResult);

        return contentEmotionResultRepository.save(contentEmotionResult);
    }

    /**
     * Emotion 설문 응답 삭제
     * @param contentEmotionResultId
     * @param memberId
     */
    @Transactional
    @Override
    public void deleteEmotionSurveyResult(Long contentEmotionResultId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        contentEmotionResultRepository.deleteById(contentEmotionResultId);

    }


}
