package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.service.ContentTextResultService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    // 선택항목 응답 기록
//    private final ContentChoiceResultService contentChoiceResultService;

    // 단일선택 응답 기록
//    @PostMapping("/content-choice-ㅂitem/{contentChoiceItemId}/content-choice-result")
//    public ResponseEntity<Long> postChoiceSurveyResult(
//            @PathVariable Long contentChoiceItemId,
//            @RequestBody ContentChoiceResultDto contentChoiceResultDto
//            ) {
//        Long contentChoiceItemId = contentChoiceResultService.postChoiceSurveyResult(contentChoiceItemId, contentChoiceResultDto);
//
////        MemberDto memberDto = memberService.updateMember(memberUpdateDto, memberId);
//
//        return ResponseEntity.ok().build();
//    }
}
