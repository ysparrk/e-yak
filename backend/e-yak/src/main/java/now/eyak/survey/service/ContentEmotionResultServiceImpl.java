package now.eyak.survey.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.*;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentEmotionResultUpdateDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.exception.DuplicatedContentResultException;
import now.eyak.survey.repository.ContentEmotionResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentEmotionResultServiceImpl implements ContentEmotionResultService {

    private final ContentEmotionResultRepository contentEmotionResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;

    private final JPAQueryFactory queryFactory;

    /**
     * Emotion 설문 응답 저장
     *
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentEmotionResult saveEmotionSurveyResult(ContentEmotionResultDto contentEmotionResultDto, Long surveyContentId, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(surveyContentId).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        if (contentEmotionResultRepository.findBySurveyContentAndMember(surveyContent, member).isPresent()) {
            throw new DuplicatedContentResultException("하루에 문항당 하나의 응답만 가능합니다.");
        }

        ContentEmotionResult contentEmotionResult = ContentEmotionResult.builder()
                .surveyContent(surveyContent)
                .choiceEmotion(contentEmotionResultDto.getChoiceEmotion())
                .member(member)
                .build();

        return contentEmotionResultRepository.save(contentEmotionResult);
    }

    /**
     * Emotion 설문 응답 수정
     *
     * @param contentEmotionResultUpdateDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentEmotionResult updateEmotionSurveyResult(ContentEmotionResultUpdateDto contentEmotionResultUpdateDto, Long surveyContentId, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(surveyContentId).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentEmotionResult contentEmotionResult = contentEmotionResultRepository.findByIdAndMember(contentEmotionResultUpdateDto.getContentEmotionResultId(), member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당하는 ContentEmotionResult가 존재하지 않습니다."));

        contentEmotionResultUpdateDto.update(contentEmotionResult);

        return contentEmotionResultRepository.save(contentEmotionResult);
    }

    /**
     * Emotion 설문 응답 삭제
     *
     * @param contentEmotionResultId
     * @param memberId
     */
    @Transactional
    @Override
    public void deleteEmotionSurveyResult(Long contentEmotionResultId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        contentEmotionResultRepository.deleteByIdAndMember(contentEmotionResultId, member);
    }

    /**
     * Emotion 설문 조회
     * 요청받은 date, 요청한 memberId
     *
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentEmotionResultResponseDto getEmotionResultsByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        QContentEmotionResult qContentEmotionResult = QContentEmotionResult.contentEmotionResult;
        QSurveyContent qSurveyContent = QSurveyContent.surveyContent;
        QSurvey qSurvey = QSurvey.survey;

        ContentEmotionResultResponseDto emotionResult = queryFactory
                .select(Projections.constructor(ContentEmotionResultResponseDto.class,
                        qContentEmotionResult.id,
                        qContentEmotionResult.member.id,
                        qContentEmotionResult.choiceEmotion,
                        qContentEmotionResult.createdAt,
                        qContentEmotionResult.updatedAt))
                .from(qContentEmotionResult)
                .leftJoin(qContentEmotionResult.surveyContent, qSurveyContent)
                .leftJoin(qSurveyContent.survey, qSurvey)
                .where(qSurvey.date.eq(date).and(qContentEmotionResult.member.eq(member)))
                .fetchOne();

        // 응답 기록이 없는 경우 id := -1로 설정 후 반환
        if (emotionResult == null) {
            return ContentEmotionResultResponseDto.builder()
                    .contentEmotionResultId(-1L)
                    .choiceEmotion(ChoiceEmotion.SOSO)
                    .memberId(-1L)
                    .createdAt(LocalDateTime.of(2023, 7, 3, 17, 30))
                    .updatedAt(LocalDateTime.of(2023, 7, 3, 17, 30))
                    .build();
        }

        return emotionResult;
    }


}
