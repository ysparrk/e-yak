package now.eyak.survey.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.survey.dto.query.SurveyContentPdfQueryDto;

import java.time.LocalDateTime;
import java.util.List;

import static now.eyak.survey.domain.QContentEmotionResult.contentEmotionResult;
import static now.eyak.survey.domain.QContentStatusResult.contentStatusResult;
import static now.eyak.survey.domain.QContentStatusResultChoiceStatusEntity.contentStatusResultChoiceStatusEntity;
import static now.eyak.survey.domain.QContentTextResult.contentTextResult;
import static now.eyak.survey.domain.QSurvey.survey;
import static now.eyak.survey.domain.QSurveyContent.surveyContent;

@RequiredArgsConstructor
public class CustomSurveyContentRepositoryImpl implements CustomSurveyContentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SurveyContentPdfQueryDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<SurveyContentPdfQueryDto> surveyContentPdfQueryDtoList = queryFactory
                .selectDistinct(
                        Projections.constructor(SurveyContentPdfQueryDto.class,
                                survey.date,
                                contentEmotionResult,
                                contentStatusResult,
                                contentTextResult
                        )
                )
                .from(survey)
                .join(survey.surveyContents, surveyContent)
                .leftJoin(surveyContent.contentTextResult, contentTextResult)
                .leftJoin(surveyContent.contentStatusResults, contentStatusResult)
                .leftJoin(surveyContent.contentEmotionResults, contentEmotionResult)
                .where(survey.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
                        .and(
                                contentEmotionResult.member.id.eq(memberId)
                                .or(contentStatusResult.member.id.eq(memberId))
                                .or(contentTextResult.member.id.eq(memberId))
                        )
                )
                .fetch();

        return surveyContentPdfQueryDtoList;
    }
}
