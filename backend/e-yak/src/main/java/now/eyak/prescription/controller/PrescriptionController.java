package now.eyak.prescription.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.dto.PrescriptionResponseDto;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.util.ApiVersionHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
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
    public ResponseEntity getAllByMemberId(@AuthenticationPrincipal Long memberId) {
        List<PrescriptionResponseDto> responseDtoList = prescriptionService.findAllByMemberId(memberId).stream().map(PrescriptionResponseDto::from
        ).toList();

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
}
