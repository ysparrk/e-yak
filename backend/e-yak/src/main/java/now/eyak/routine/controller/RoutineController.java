package now.eyak.routine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.dto.request.MedicineRoutineCheckIdDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.dto.response.MedicineRoutineCheckIdResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineDateResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthDateDto;
import now.eyak.routine.dto.response.PdfResponseDto;
import now.eyak.routine.service.MedicineRoutineCheckService;
import now.eyak.routine.service.PdfService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/medicine-routine-checks")
@RequiredArgsConstructor
public class RoutineController {

    private final MedicineRoutineCheckService medicineRoutineCheckService;
    private final PdfService pdfService;

    /**
     * 약 복용 체크 기록 및 수정(true or false)
     * @param medicineRoutineCheckUpdateDto
     * @param memberId
     * @return
     */
    @Operation(summary = "Medicine Check", description = "알람을 들은 후 약 복용을 체크/취소 합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineCheckUpdateDto.class)))
    @PostMapping
    public ResponseEntity updateMedicineRoutineCheck(
            @RequestBody MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto,
            @AuthenticationPrincipal Long memberId
        ) {

        MedicineRoutineCheck medicineRoutineCheck = medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto, memberId);

        return ResponseEntity.ok().build();
    }

    /**
     * 하루 단위 복용량 조회
     * @param date
     * @param memberId
     * @return
     */
    @Operation(summary = "Day MedicineCheck", description = "요청받은 날짜의 복용량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineMonthDateDto.class)))
    @GetMapping("/day")
    public ResponseEntity getDateResultsByDateAndMember(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal Long memberId
            ) {

        MedicineRoutineMonthDateDto dateResultsByDateAndMember = medicineRoutineCheckService.getDateResultsByDateAndMember(date, memberId);

        return ResponseEntity.ok(dateResultsByDateAndMember);

    }

    /**
     * 한달 단위 복용량 조회
     * @param yearMonth
     * @param memberId
     * @return
     */
    @Operation(summary = "Get Month MedicineCheck", description = "요청받은 달의 날짜별 복용량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineMonthDateDto.class)))
    @GetMapping("/month")
    public ResponseEntity getMonthResultsByMonthAndMember(
            @RequestParam YearMonth yearMonth,
            @RequestParam(required = false) Long requeteeId,
            @AuthenticationPrincipal Long memberId
        ) {

        List<MedicineRoutineMonthDateDto>  monthResultsByMonthAndMember = medicineRoutineCheckService.getMonthResultsByMonthAndMember(yearMonth, memberId, requeteeId);

        return ResponseEntity.ok(monthResultsByMonthAndMember);
    }

    /**
     * 하루 단위 복용 상세 조회
     * @param date
     * @param requesteeId
     * @return
     */
    @Operation(summary = "Get Day Detail", description = "요청받은 날짜의 복용 상세와 건강설문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineDateResponseDto.class)))
    @GetMapping("/day-detail")
    public ResponseEntity getDateDetailResultsByDateAndMember(
            @RequestParam LocalDate date,
            @RequestParam(value = "requeteeId", required = false) Long requesteeId,
            @AuthenticationPrincipal Long requesterId
        ) {

        // 사용자(requesterId)가 본인의 복약 상세 조회를 요청한 경우
        MedicineRoutineDateResponseDto dateDetailResultsByDateAndMember = medicineRoutineCheckService.getDateDetailResultsByDateAndMember(date, requesterId, requesteeId);
        return ResponseEntity.ok(dateDetailResultsByDateAndMember);


    }

    /**
     * MedicineRoutineCheck의 id 조회
     * @param medicineRoutineCheckIdDto
     * @param memberId
     * @return
     */
    @Operation(summary = "Get MedicineRoutineCheck id", description = "MedicineRoutineCheck의 id를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineCheckIdResponseDto.class)))
    @PostMapping("/id")
    public ResponseEntity getMedicineRoutineCheckId(
            @RequestBody MedicineRoutineCheckIdDto medicineRoutineCheckIdDto,
            @AuthenticationPrincipal Long memberId
            ) {

        MedicineRoutineCheckIdResponseDto medicineRoutineCheckId = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, memberId);

        return ResponseEntity.ok(medicineRoutineCheckId);
    }


    @Operation(summary = "Get PDF Results", description = "요청받은 기간의 복용중인 약과, 설문조사를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PdfResponseDto.class)))
    @GetMapping("/pdf")
    public ResponseEntity getPdfResults(
            @AuthenticationPrincipal Long memberId,
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime endDateTime
            ) throws URISyntaxException {

        log.debug("memberId: {}", memberId);

        PdfResponseDto pdfResponseByDatesAndMember = pdfService.getPdfResponseByDatesAndMember(memberId, startDateTime, endDateTime);

        log.debug("pdfResponseDto = {}", pdfResponseByDatesAndMember);

        return ResponseEntity.ok(pdfResponseByDatesAndMember);
    }

}
