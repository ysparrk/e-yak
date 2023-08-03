package now.eyak.survey.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.*;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.repository.ContentEmotionResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentEmotionResultServiceImpl implements ContentEmotionResultService {

    private final ContentEmotionResultRepository contentEmotionResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;

    private final JPAQueryFactory queryFactory;
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

    /**
     * Emotion 설문 조회
     * 요청받은 date, 요청한 memberId
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<ContentEmotionResultResponseDto> getEmotionResultsByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        Survey survey = surveyRepository.findByDate(date).orElseThrow(() -> new NoSuchElementException("해당하는 날짜의 설문기록이 없습니다."));

        QContentEmotionResult qContentEmotionResult = QContentEmotionResult.contentEmotionResult;
        QSurveyContent qSurveyContent = QSurveyContent.surveyContent;
        QSurvey qSurvey = QSurvey.survey;

        List<ContentEmotionResultResponseDto> emotionResults = queryFactory
                .select(Projections.constructor(ContentEmotionResultResponseDto.class,
                        qContentEmotionResult.member.id,
                        qContentEmotionResult.choiceEmotion,
                        qContentEmotionResult.createdAt,
                        qContentEmotionResult.updatedAt))
                .from(qContentEmotionResult)
                .leftJoin(qContentEmotionResult.surveyContent, qSurveyContent)
                .leftJoin(qSurveyContent.survey, qSurvey)
                .where(qSurvey.date.eq(date).and(qContentEmotionResult.member.eq(member)))
                .fetch();

        return emotionResults;

    }


}
