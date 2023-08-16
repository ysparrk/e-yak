package now.eyak.survey.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.query.SurveyContentPdfQueryDto;
import now.eyak.survey.enumeration.SurveyContentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static now.eyak.survey.domain.QContentEmotionResult.contentEmotionResult;
import static now.eyak.survey.domain.QContentStatusResult.contentStatusResult;
import static now.eyak.survey.domain.QContentTextResult.contentTextResult;
import static now.eyak.survey.domain.QSurvey.survey;
import static now.eyak.survey.domain.QSurveyContent.surveyContent;

@RequiredArgsConstructor
public class CustomSurveyContentRepositoryImpl implements CustomSurveyContentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SurveyContentPdfQueryDto> findAllByMemberAndBetweenDates(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        List<ContentEmotionResult> emotionResults = queryFactory
                .select(contentEmotionResult)
                .from(surveyContent)
                .leftJoin(surveyContent.survey, survey)
                .leftJoin(surveyContent.contentEmotionResults, contentEmotionResult)
                .where(survey.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
                        .and(contentEmotionResult.member.id.eq(memberId).or(contentEmotionResult.id.isNull()))
                        .and(surveyContent.surveyContentType.eq(SurveyContentType.CHOICE_EMOTION))
                )
                .orderBy(survey.date.asc())
                .fetch();

        List<ContentStatusResult> statusResults = queryFactory
                .select(contentStatusResult)
                .from(surveyContent)
                .leftJoin(surveyContent.survey, survey)
                .leftJoin(surveyContent.contentStatusResults, contentStatusResult)
                .where(survey.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
                        .and(contentStatusResult.member.id.eq(memberId).or(contentStatusResult.id.isNull()))
                        .and(surveyContent.surveyContentType.eq(SurveyContentType.CHOICE_STATUS))
                )
                .orderBy(survey.date.asc())
                .fetch();

        List<ContentTextResult> textResults = queryFactory
                .select(contentTextResult)
                .from(surveyContent)
                .leftJoin(surveyContent.survey, survey)
                .leftJoin(surveyContent.contentTextResult, contentTextResult)
                .where(survey.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
                        .and(contentTextResult.member.id.eq(memberId).or(contentTextResult.id.isNull()))
                        .and(surveyContent.surveyContentType.eq(SurveyContentType.TEXT))
                )
                .orderBy(survey.date.asc())
                .fetch();

        List<LocalDate> dateList = queryFactory
                .selectDistinct(survey.date)
                .from(surveyContent)
                .join(surveyContent.survey, survey)
                .where(survey.date.between(startDateTime.toLocalDate(), endDateTime.toLocalDate()))
                .orderBy(survey.date.asc())
                .fetch();


        List<SurveyContentPdfQueryDto> surveyContentPdfQueryDtoList = new ArrayList<>();
        for (int idx = 0; idx < dateList.size(); idx++) {
            SurveyContentPdfQueryDto surveyContentPdfQueryDto = SurveyContentPdfQueryDto.builder()
                    .date(dateList.get(idx))
                    .contentEmotionResult(emotionResults.get(idx))
                    .contentStatusResult(statusResults.get(idx))
                    .contentTextResult(textResults.get(idx))
                    .build();

            surveyContentPdfQueryDtoList.add(surveyContentPdfQueryDto);
        }

        return surveyContentPdfQueryDtoList;
    }
}
