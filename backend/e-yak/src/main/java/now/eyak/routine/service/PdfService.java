package now.eyak.routine.service;

import now.eyak.routine.dto.response.PdfResponseDto;

import java.time.LocalDateTime;

public interface PdfService {
    PdfResponseDto getPdfResponseByDatesAndMember(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime); // 요청받은 기간에 대한 pdf에 들어갈 내용 조회

}
