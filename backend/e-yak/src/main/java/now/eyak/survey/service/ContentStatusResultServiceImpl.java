package now.eyak.survey.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.*;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.request.ContentStatusResultUpdateDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;
import now.eyak.survey.enumeration.ChoiceStatus;
import now.eyak.survey.exception.DuplicatedContentResultException;
import now.eyak.survey.repository.ContentStatusResultRepository;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContentStatusResultServiceImpl implements ContentStatusResultService {

    private final ContentStatusResultRepository contentStatusResultRepository;
    private final SurveyContentRepository surveyContentRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;

    private final JPAQueryFactory queryFactory;

    /**
     * Status 설문 응답 저장
     *
     * @param contentStatusResultDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentStatusResult saveStatusSurveyResult(ContentStatusResultDto contentStatusResultDto, Long surveyContentId, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(surveyContentId).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        if (contentStatusResultRepository.findBySurveyContentAndMember(surveyContent, member).isPresent()) {
            throw new DuplicatedContentResultException("하루에 문항당 하나의 응답만 가능합니다.");
        }

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
     * @param contentStatusResultUpdateDto
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentStatusResult updateStatusSurveyResult(ContentStatusResultUpdateDto contentStatusResultUpdateDto, Long surveyContentId, Long memberId) {
        SurveyContent surveyContent = surveyContentRepository.findById(surveyContentId).orElseThrow(() -> new NoSuchElementException("surveyContentId에 해당하는 SurveyContent가 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        ContentStatusResult contentStatusResult = contentStatusResultRepository.findByIdAndMember(contentStatusResultUpdateDto.getContentStatusResultId(), member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당하는 ContentStatusResult가 존재하지 않습니다."));

        contentStatusResult.setSelectedStatusChoices(new ArrayList<>(contentStatusResultUpdateDto.getSelectedStatusChoices()));
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

        contentStatusResultRepository.deleteByIdAndMember(contentStatusResultId, member);
    }

    /**
     * Status 설문 조회
     *
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public ContentStatusResultResponseDto getStatusResultByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        QContentStatusResult qContentStatusResult = QContentStatusResult.contentStatusResult;
        QSurveyContent qSurveyContent = QSurveyContent.surveyContent;
        QSurvey qSurvey = QSurvey.survey;

        // selectedStatusChoices 제외하고 ContentStatusResult.id 조회
        Tuple findStatusResult = queryFactory
                .select(qContentStatusResult.id,
                        qContentStatusResult.member.id,
                        qContentStatusResult.createdAt,
                        qContentStatusResult.updatedAt
                )
                .from(qContentStatusResult)
                .leftJoin(qContentStatusResult.surveyContent, qSurveyContent)
                .leftJoin(qSurveyContent.survey, qSurvey)
                .where(qSurvey.date.eq(date).and(qContentStatusResult.member.eq(member)))
                .fetchOne();
        
        // 응답 기록이 없는 경우 id := -1로 설정 후 반환
        if (findStatusResult == null) {
            return ContentStatusResultResponseDto.builder()
                    .contentStatusResultId(-1L)
                    .build();
        }

        Long contentStatusResultId = findStatusResult.get(qContentStatusResult.id);
        Long memberQueryId = findStatusResult.get(qContentStatusResult.member.id);
        LocalDateTime createdAt = findStatusResult.get(qContentStatusResult.createdAt);
        LocalDateTime updatedAt = findStatusResult.get(qContentStatusResult.updatedAt);

        ContentStatusResult contentStatusResult = contentStatusResultRepository.findById(contentStatusResultId).orElseThrow(() -> new NoSuchElementException("해당하는 ContentStatusResult가 없습니다."));
        List<ChoiceStatus> selectedStatusChoices = contentStatusResult.getSelectedStatusChoices();

        ContentStatusResultResponseDto result = ContentStatusResultResponseDto.builder()
                .contentStatusResultId(contentStatusResultId)
                .memberId(memberQueryId)
                .selectedStatusChoices(selectedStatusChoices)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        return result;
    }
}