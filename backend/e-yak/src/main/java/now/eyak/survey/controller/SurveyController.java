package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.survey.dto.ContentTextResultDto;
import now.eyak.survey.service.ContentTextResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SurveyController {

    private final ContentTextResultService contentTextResultService;
    // text 문항 응답 기록
    @PostMapping("/survey-contents/{surveyContentId}/content-text-result")
    public ResponseEntity<Long> postTextSurveyResult(
            @PathVariable Long surveyContentId,
            @RequestBody ContentTextResultDto contentTextResultDto,
            @RequestParam Long memberId) {
        Member member = new Member(); // Member 엔티티에 대한 ID만 필요하므로 ID를 가진 객체를 생성해서 넣어줍니다.
        member.setId(memberId);  // 멤버 객체에 id 설정
        Long contentTextResultId = contentTextResultService.postTextSurveyResult(surveyContentId, contentTextResultDto, member);
        return new ResponseEntity<>(contentTextResultId, HttpStatus.CREATED);
    }

    // 설문 조회
//    @GetMapping("/surveys/{surveyId}")
//    public ResponseEntity<List<ContentTextResult>> getTextSurveyResultsBySurveyContentId(
//            @PathVariable Long surveyContentId) {
//        List<ContentTextResult> results = textSurveyService.getTextSurveyResultsBySurveyContentId(surveyContentId);
//        return new ResponseEntity<>(results, HttpStatus.OK);
//    }

}
