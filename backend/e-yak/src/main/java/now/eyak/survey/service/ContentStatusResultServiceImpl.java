package now.eyak.survey.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.survey.domain.*;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;
import now.eyak.survey.enumeration.ChoiceStatus;
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

    /**
     * Status 설문 조회
     * @param date
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<ContentStatusResultResponseDto> getStatusResultsByDateAndMember(LocalDate date, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));
        Survey survey = surveyRepository.findByDate(date).orElseThrow(() -> new NoSuchElementException("해당하는 날짜의 설문기록이 없습니다."));

        QContentStatusResult qContentStatusResult = QContentStatusResult.contentStatusResult;
        QSurveyContent qSurveyContent = QSurveyContent.surveyContent;
        QSurvey qSurvey = QSurvey.survey;

        // selectedStatusChoices 제외하고 ContentStatusResult.id 조회
        List<Tuple> findStatusResultId = queryFactory
                .select(qContentStatusResult.id,
                        qContentStatusResult.member.id,
                        qContentStatusResult.createdAt,
                        qContentStatusResult.updatedAt)
                .from(qContentStatusResult)
                .leftJoin(qContentStatusResult.surveyContent, qSurveyContent)
                .leftJoin(qSurveyContent.survey, qSurvey)
                .where(qSurvey.date.eq(date).and(qContentStatusResult.member.eq(member)))
                .fetch();

        // 리스트 선언
       List<ContentStatusResultResponseDto> statusList = new ArrayList<>();

        for (Tuple tuple : findStatusResultId) {
            Long contentStatusResultId = tuple.get(qContentStatusResult.id);
            Long memberQueryId = tuple.get(qContentStatusResult.member.id);
            LocalDateTime createdAt = tuple.get(qContentStatusResult.createdAt);
            LocalDateTime updatedAt = tuple.get(qContentStatusResult.updatedAt);

            ContentStatusResult contentStatusResult = contentStatusResultRepository.findById(contentStatusResultId).orElseThrow(() -> new NoSuchElementException("해당하는 ContentStatusResult가 없습니다."));
            List<ChoiceStatus> selectedStatusChoices = contentStatusResult.getSelectedStatusChoices();

            ContentStatusResultResponseDto result = ContentStatusResultResponseDto.builder()
                    .id(contentStatusResultId)
                    .memberId(memberQueryId)
                    .selectedStatusChoices(selectedStatusChoices)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            statusList.add(result);
        }
        return statusList;
    }
}