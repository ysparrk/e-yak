package now.eyak.prescription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import now.eyak.member.dto.response.SignInResponseDto;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.MedicineRoutineResponseDto;
import now.eyak.prescription.dto.MedicineRoutineUpdateDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final ApiVersionHolder apiVersionHolder;
    private final PrescriptionService prescriptionService;

    @Operation(summary = "Save Prescription", description = "처방전 단위의 복약 정보를 등록한다.")
    @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = PrescriptionDto.class)))
    @PostMapping
    public ResponseEntity post(@RequestBody PrescriptionDto prescriptionDto, @AuthenticationPrincipal Long memberId) throws URISyntaxException {
        Prescription prescription = prescriptionService.insert(prescriptionDto, memberId);

        return ResponseEntity.created((new URI(apiVersionHolder.getVersion() + "/prescriptions/" + prescription.getId()))).build();
    }

    @Operation(summary = "GET All Prescription", description = "복약 정보 전체 조회")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class)))
    @GetMapping
    public ResponseEntity getAllByMemberId(@RequestParam(required = false) LocalDateTime dateTime, @AuthenticationPrincipal Long memberId) {
        List<PrescriptionResponseDto> responseDtoList = null;
        if (dateTime == null) {
            responseDtoList = prescriptionService.findAllByMemberId(memberId).stream().map(PrescriptionResponseDto::from).toList();
        } else {
            responseDtoList = prescriptionService.findAllByMemberIdBetweenDate(memberId, dateTime).stream().map(PrescriptionResponseDto::from).toList();
        }

        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "Get All Prescription per Day", description = "사용자의 해당 date에 복용해야하는 약 전체 목록 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class)))
    @GetMapping("/{prescriptionId}")
    public ResponseEntity getById(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        Prescription prescription = prescriptionService.findById(prescriptionId, memberId);

        return ResponseEntity.ok(PrescriptionResponseDto.from(prescription));
    }

    @Operation(summary = "Modify Prescription", description = "사용자의 복약 정보를 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PrescriptionResponseDto.class)))
    @PutMapping("/{prescriptionId}")
    public ResponseEntity put(@PathVariable Long prescriptionId, @RequestBody PrescriptionDto prescriptionDto, @AuthenticationPrincipal Long memberId) {
        Prescription prescription = prescriptionService.update(prescriptionId, prescriptionDto, memberId);

        return ResponseEntity.ok(PrescriptionResponseDto.from(prescription));
    }

    @Operation(summary = "Delete Prescription", description = "사용자의 처방전 단의 복약 정보를 하나 삭제한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PrescriptionDto.class)))
    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity delete(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        prescriptionService.delete(prescriptionId, memberId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Prescription Routine", description = "사용자의 복약정보의 루틴을 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineResponseDto.class)))
    @GetMapping("/{prescriptionId}/routines")
    public ResponseEntity getMedicineRoutines(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        List<MedicineRoutineResponseDto> medicineRoutineResponseDtoList = prescriptionService.findPrescriptionMedicineRoutinesById(prescriptionId, memberId).stream().map(prescriptionMedicineRoutine -> {
            return MedicineRoutineResponseDto.from(prescriptionMedicineRoutine);
        }).toList();

        return ResponseEntity.ok(medicineRoutineResponseDtoList);
    }

    @Operation(summary = "Modify Prescription Routine", description = "사용자의 복약정보의 루틴을 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MedicineRoutineUpdateDto.class)))
    @PutMapping("/{prescriptionId}/routines")
    public ResponseEntity putMedicineRoutines(@RequestBody MedicineRoutineUpdateDto medicineRoutineUpdateDto, @PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        prescriptionService.updatePrescriptionMedicineRoutinesById(medicineRoutineUpdateDto, prescriptionId, memberId);

        return ResponseEntity.ok().build();
    }
}
