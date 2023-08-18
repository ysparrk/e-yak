package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.request.*;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;
import now.eyak.survey.dto.response.SurveyContentDto;
import now.eyak.survey.service.ContentEmotionResultService;
import now.eyak.survey.service.ContentStatusResultService;
import now.eyak.survey.service.ContentTextResultService;
import now.eyak.survey.service.SurveyContentService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SurveyResultController {

    private final ApiVersionHolder apiVersionHolder;
    private final ContentTextResultService contentTextResultService;
    private final ContentEmotionResultService contentEmotionResultService;
    private final ContentStatusResultService contentStatusResultService;
    private final SurveyContentService surveyContentService;

    /**
     * daily 설문 조회
     *
     * @param date
     * @param memberId
     * @return
     */
    @GetMapping("/survey-results")
    public ResponseEntity getSurveyResult(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal Long memberId
    ) {

        SurveyContentDto surveyResultByDateAndMember = surveyContentService.getSurveyResultByDateAndMember(date, memberId);

        return ResponseEntity.ok(surveyResultByDateAndMember);
    }

    /**
     * Text설문 응답2
     *
     * @param contentTextResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-text-results")
    public ResponseEntity saveTextSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentTextResultDto contentTextResultDto,
            @AuthenticationPrincipal Long memberId
    ) throws URISyntaxException {

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContentId, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-text-results/" + contentTextResult.getId())).build();
    }

    /**
     * Text 설문 응답 수정
     *
     * @param contentTextResultUpdateDto
     * @param memberId
     * @return
     */
    @PutMapping("/survey-contents/{surveyContentId}/content-text-results")
    public ResponseEntity updateTextSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentTextResultUpdateDto contentTextResultUpdateDto,
            @AuthenticationPrincipal Long memberId
    ) {

        ContentTextResult contentTextResult = contentTextResultService.updateTextSurveyResult(contentTextResultUpdateDto, surveyContentId, memberId);

        return ResponseEntity.ok(ContentTextResultResponseDto.of(contentTextResult));
    }

    /**
     * Text 설문 응답 삭제
     *
     * @param memberId
     * @param contentTextResultId
     * @return
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-text-results/{contentTextResultId}")
    public ResponseEntity deleteTextSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentTextResultId
    ) {

        contentTextResultService.deleteTextSurveyResult(contentTextResultId, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Emotion설문 응답
     *
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-emotion-results")
    public ResponseEntity saveEmotionSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentEmotionResultDto contentEmotionResultDto,
            @AuthenticationPrincipal Long memberId
    ) throws URISyntaxException {

        ContentEmotionResult contentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, surveyContentId, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-emotion-results/" + contentEmotionResult.getId())).build();
    }

    /**
     * Emotion 설문 응답 수정
     *
     * @param surveyContentId
     * @param contentEmotionResultUpdateDto
     * @param memberId
     * @return
     */
    @PutMapping("/survey-contents/{surveyContentId}/content-emotion-results")
    public ResponseEntity updateEmotionSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentEmotionResultUpdateDto contentEmotionResultUpdateDto,
            @AuthenticationPrincipal Long memberId
    ) {

        ContentEmotionResult contentEmotionResult = contentEmotionResultService.updateEmotionSurveyResult(contentEmotionResultUpdateDto, surveyContentId, memberId);

        return ResponseEntity.ok(ContentEmotionResultResponseDto.of(contentEmotionResult));
    }

    /**
     * Emotion설문 응답 삭제
     *
     * @param memberId
     * @param contentEmotionResultId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-emotion-results/{contentEmotionResultId}")
    public ResponseEntity deleteEmotionSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentEmotionResultId
    ) throws URISyntaxException {

        contentEmotionResultService.deleteEmotionSurveyResult(contentEmotionResultId, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Status 설문 응답 저장
     *
     * @param contentStatusResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-status-results")
    public ResponseEntity saveStatusSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentStatusResultDto contentStatusResultDto,
            @AuthenticationPrincipal Long memberId
    ) throws URISyntaxException {
        ContentStatusResult contentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, surveyContentId, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-status-results/" + contentStatusResult.getId())).build();
    }

    /**
     * Status 설문 응답 수정
     *
     * @param surveyContentId
     * @param contentStatusResultUpdateDto
     * @param memberId
     * @return
     */
    @PutMapping("/survey-contents/{surveyContentId}/content-status-results")
    public ResponseEntity updateStatusSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentStatusResultUpdateDto contentStatusResultUpdateDto,
            @AuthenticationPrincipal Long memberId
    ) {
        ContentStatusResult contentStatusResult = contentStatusResultService.updateStatusSurveyResult(contentStatusResultUpdateDto, surveyContentId, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Status 설문 응답 삭제
     *
     * @param memberId
     * @param contentStatusResultId
     * @return
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-status-results/{contentStatusResultId}")
    public ResponseEntity deleteStatusSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentStatusResultId
    ) {

        contentStatusResultService.deleteStatusSurveyResult(contentStatusResultId, memberId);

        return ResponseEntity.ok().build();
    }

}
