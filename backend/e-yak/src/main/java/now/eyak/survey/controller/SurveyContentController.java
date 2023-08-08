package now.eyak.survey.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.survey.dto.response.SurveyContentIdResponseDto;
import now.eyak.survey.service.SurveyContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SurveyContentController {
    private final SurveyContentService surveyContentService;

    @GetMapping("/survey-contents")
    public ResponseEntity getSurveyContentByDate(@RequestParam LocalDate date) {
        List<SurveyContentIdResponseDto> surveyContentByDate = surveyContentService.getSurveyContentByDate(date).stream().map(SurveyContentIdResponseDto::from).toList();

        return ResponseEntity.ok(surveyContentByDate);
    }
}
