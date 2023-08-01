package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.dto.ContentTextResultUpdateDto;
import now.eyak.survey.service.ContentTextResultService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SurveyController {

    private final ApiVersionHolder apiVersionHolder;
    private final ContentTextResultService contentTextResultService;

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
     * Text설문 응답 삭제
     * @param contentTextResultDto
     * @param memberId
     * @return
     * @throws URISyntaxException
     */
    @DeleteMapping("/survey-contents/{surveyContentId}/content-text-result/{contentTextResultId}")
    public ResponseEntity deleteTextSurveyResult(
            @RequestBody ContentTextResultDto contentTextResultDto,
            @AuthenticationPrincipal Long memberId
        ) throws URISyntaxException {


        return ResponseEntity.ok().build();
    }

}
