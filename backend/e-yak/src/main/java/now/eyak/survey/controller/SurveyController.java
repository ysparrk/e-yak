package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentEmotionResult;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.ContentEmotionResultDto;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.dto.ContentTextResultUpdateDto;
import now.eyak.survey.service.ContentEmotionResultService;
import now.eyak.survey.service.ContentTextResultService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/survey-contents/{surveyContentId}")
@RequiredArgsConstructor
public class SurveyController {

    private final ApiVersionHolder apiVersionHolder;
    private final ContentTextResultService contentTextResultService;
    private final ContentEmotionResultService contentEmotionResultService;

    /**
     * Text설문 응답
     * @param contentTextResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/content-text-result")
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
    @PatchMapping("/content-text-result")
    public ResponseEntity updateTextSurveyResult(
            @RequestBody ContentTextResultUpdateDto contentTextResultUpdateDto,
            @AuthenticationPrincipal Long memberId
        ) throws URISyntaxException {

        ContentTextResult contentTextResult = contentTextResultService.updateTextSurveyResult(contentTextResultUpdateDto, memberId);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/content-text-result/{contentTextResultId}")
    public ResponseEntity deleteTextSurveyResult(
            @RequestBody ContentTextResultDto contentTextResultDto,
            @AuthenticationPrincipal Long memberId,
            ) throws URISyntaxException {


        return ResponseEntity.ok().build();
    }

    /**
     * Emotion설문 응답
     * @param contentEmotionResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/content-emotion-result")
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
    @PutMapping("/content-emotion-result")
    public ResponseEntity updateEmotionSurveyResult(
            @RequestBody ContentEmotionResultDto contentEmotionResultDto,
            @AuthenticationPrincipal Long memberId
            ) throws URISyntaxException {

        ContentEmotionResult contentEmotionResult = contentEmotionResultService.updateEmotionSurveyResult(contentEmotionResultDto, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * Emotion설문 응답 삭제
     * @param contentEmotionResultDto
     * @param memberId
     * @param contentEmotionResultId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/content-emotion-result/{contentEmotionResultId}")
    public ResponseEntity deleteEmotionSurveyResult(
            @RequestBody ContentEmotionResultDto contentEmotionResultDto,
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long contentEmotionResultId
            ) throws URISyntaxException {

        return ResponseEntity.ok().build();
    }


}
