package now.eyak.prescription.controller;

import lombok.RequiredArgsConstructor;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.MedicineRoutineResponseDto;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping
    public ResponseEntity post(@RequestBody PrescriptionDto prescriptionDto, @AuthenticationPrincipal Long memberId) throws URISyntaxException {
        Prescription prescription = prescriptionService.insert(prescriptionDto, memberId);

        return ResponseEntity.created((new URI(apiVersionHolder.getVersion() + "/prescriptions/" + prescription.getId()))).build();
    }

    @GetMapping
    public ResponseEntity getAllByMemberId(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateTime, @AuthenticationPrincipal Long memberId) {
        List<PrescriptionResponseDto> responseDtoList = null;
        if (dateTime == null) {
            responseDtoList = prescriptionService.findAllByMemberId(memberId).stream().map(PrescriptionResponseDto::from).toList();
        } else {
            responseDtoList = prescriptionService.findAllByMemberIdBetweenDate(memberId, dateTime).stream().map(PrescriptionResponseDto::from).toList();
        }

        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity getById(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        Prescription prescription = prescriptionService.findById(prescriptionId, memberId);

        return ResponseEntity.ok(PrescriptionResponseDto.from(prescription));
    }

    @PutMapping("/{prescriptionId}")
    public ResponseEntity put(@PathVariable Long prescriptionId, @RequestBody PrescriptionDto prescriptionDto, @AuthenticationPrincipal Long memberId) {
        Prescription prescription = prescriptionService.update(prescriptionId, prescriptionDto, memberId);

        return ResponseEntity.ok(PrescriptionResponseDto.from(prescription));
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity delete(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        prescriptionService.delete(prescriptionId, memberId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{prescriptionId}/routines")
    public ResponseEntity getMedicineRoutines(@PathVariable Long prescriptionId, @AuthenticationPrincipal Long memberId) {
        List<MedicineRoutineResponseDto> medicineRoutineResponseDtoList = prescriptionService.findPrescriptionMedicineRoutinesById(prescriptionId, memberId).stream().map(prescriptionMedicineRoutine -> {
            return MedicineRoutineResponseDto.from(prescriptionMedicineRoutine);
        }).toList();

        return ResponseEntity.ok(medicineRoutineResponseDtoList);
    }
}
