package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentStatusResult;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.request.ContentEmotionResultDto;
import now.eyak.survey.dto.request.ContentStatusResultDto;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.dto.request.ContentTextResultUpdateDto;
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
import java.util.List;

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
     * @param date
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/survey-results")
    public ResponseEntity getSurveyResult(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {

        List<SurveyContentDto> surveyResultByDateAndMember = surveyContentService.getSurveyResultByDateAndMember(date, memberId);

        return ResponseEntity.ok(surveyResultByDateAndMember);
    }


    /**
     * Text설문 응답
     * @param contentTextResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-text-result")
    public ResponseEntity saveTextSurveyResult(
            @RequestBody ContentTextResultDto contentTextResultDto,
            @AuthenticationPrincipal Long memberId
        ) throws URISyntaxException {

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-text-results/" + contentTextResult.getId())).build();
    }

    /**
     * Text설문 응답 수정
     * @param contentTextResultUpdateDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PatchMapping("/survey-contents/{surveyContentId}/content-text-result")
    public ResponseEntity updateTextSurveyResult(
            @RequestBody ContentTextResultUpdateDto contentTextResultUpdateDto,
            @AuthenticationPrincipal Long memberId
        ) throws URISyntaxException {

        ContentTextResult contentTextResult = contentTextResultService.updateTextSurveyResult(contentTextResultUpdateDto, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Text 설문 응답 삭제
     * @param memberId
     * @param contentTextResultId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-text-result/{contentTextResultId}")
    public ResponseEntity deleteTextSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentTextResultId
            ) throws URISyntaxException {

        contentTextResultService.deleteTextSurveyResult(contentTextResultId, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Emotion설문 응답
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-emotion-result")
    public ResponseEntity saveEmotionSurveyResult(
            @RequestBody ContentEmotionResultDto contentEmotionResultDto,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {

        ContentEmotionResult contentEmotionResult = contentEmotionResultService.saveEmotionSurveyResult(contentEmotionResultDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-emotion-results/" + contentEmotionResult.getId())).build();
    }

    /**
     * Emotion설문 응답 수정
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/survey-contents/{surveyContentId}/content-emotion-result")
    public ResponseEntity updateEmotionSurveyResult(
            @RequestBody ContentEmotionResultDto contentEmotionResultDto,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {

        ContentEmotionResult contentEmotionResult = contentEmotionResultService.updateEmotionSurveyResult(contentEmotionResultDto, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Emotion설문 응답 삭제
     * @param memberId
     * @param contentEmotionResultId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-emotion-result/{contentEmotionResultId}")
    public ResponseEntity deleteEmotionSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentEmotionResultId
            ) throws URISyntaxException {

        contentEmotionResultService.deleteEmotionSurveyResult(contentEmotionResultId, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Status 설문 응답 저장
     * @param contentStatusResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/survey-contents/{surveyContentId}/content-status-result")
    public ResponseEntity saveStatusSurveyResult(
            @RequestBody ContentStatusResultDto contentStatusResultDto,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {
        ContentStatusResult contentStatusResult = contentStatusResultService.saveStatusSurveyResult(contentStatusResultDto, memberId);

        return ResponseEntity.created(new URI(apiVersionHolder.getVersion() + "/content-status-results/" + contentStatusResult.getId())).build();
    }

    /**
     * Status 설문 응답 수정
     * @param contentStatusResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/survey-contents/{surveyContentId}/content-status-result")
    public ResponseEntity updateStatusSurveyResult(
            @RequestBody ContentStatusResultDto contentStatusResultDto,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {
        ContentStatusResult contentStatusResult = contentStatusResultService.updateStatusSurveyResult(contentStatusResultDto, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Status 설문 응답 삭제
     * @param memberId
     * @param contentStatusResultId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-status-result/{contentStatusResultId}")
    public ResponseEntity deleteStatusSurveyResult(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentStatusResultId
            ) throws URISyntaxException {

        contentStatusResultService.deleteStatusSurveyResult(contentStatusResultId, memberId);

        return ResponseEntity.ok().build();
    }

}
