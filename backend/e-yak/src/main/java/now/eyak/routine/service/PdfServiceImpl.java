package now.eyak.routine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.dto.query.PrescriptionListQueryDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.routine.dto.response.PdfResponseDto;
import now.eyak.survey.dto.query.SurveyContentPdfQueryDto;
import now.eyak.survey.dto.response.ContentEmotionResultResponseDto;
import now.eyak.survey.dto.response.ContentStatusResultResponseDto;
import now.eyak.survey.dto.response.ContentTextResultResponseDto;
import now.eyak.survey.dto.response.SurveyContentPdfResponseDto;
import now.eyak.survey.repository.SurveyContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final MemberRepository memberRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final SurveyContentRepository surveyContentRepository;

    @Transactional
    @Override
    public PdfResponseDto getPdfResponseByDatesAndMember(Long memberId, LocalDateTime startDateTime,
                                                         LocalDateTime endDateTime) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        List<PrescriptionListQueryDto> prescriptionList = prescriptionRepository.findAllByMemberAndBetweenDates(member,
                startDateTime, endDateTime);
        List<SurveyContentPdfQueryDto> surveyContentPdfQueryDtoList = surveyContentRepository.findAllByMemberAndBetweenDates(
                member.getId(), startDateTime, endDateTime);

        List<SurveyContentPdfResponseDto> surveyContentDtoList = surveyContentPdfQueryDtoList.stream()
                .map(surveyContentPdfQueryDto ->
                        SurveyContentPdfResponseDto.builder()
                                .date(surveyContentPdfQueryDto.getDate())
                                .contentEmotionResultResponse(ContentEmotionResultResponseDto.of(
                                        surveyContentPdfQueryDto.getContentEmotionResult()))
                                .contentStatusResultResponse(ContentStatusResultResponseDto.of(
                                        surveyContentPdfQueryDto.getContentStatusResult()))
                                .contentTextResultResponse(ContentTextResultResponseDto.of(
                                        surveyContentPdfQueryDto.getContentTextResult()))
                                .build())
                .toList();

        PdfResponseDto pdfResponseDto = PdfResponseDto.builder()
                .prescriptionList(prescriptionList)
                .surveyContentList(surveyContentDtoList)
                .build();

        log.debug("[log] prescriptionList: {}", prescriptionList);
        log.debug("[log] surveyContentPdfQueryDtoList.size(): {}", surveyContentPdfQueryDtoList.size());
        log.debug("[log] surveyContentDtoList: {}", surveyContentDtoList);

        return pdfResponseDto;
    }
}
