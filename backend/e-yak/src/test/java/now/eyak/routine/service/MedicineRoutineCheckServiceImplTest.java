package now.eyak.routine.service;

import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.dto.MedicineRoutineCheckDto;
import now.eyak.routine.dto.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@EnableScheduling  // 스케줄링 활성화
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MedicineRoutineCheckServiceImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MedicineRoutineCheckService medicineRoutineCheckService;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    PrescriptionService prescriptionService;
    @Autowired
    MedicineRoutineCheckRepository medicineRoutineCheckRepository;
    @Autowired
    MedicineRoutineRepository medicineRoutineRepository;


    Member member;
    Prescription prescription;
    PrescriptionDto prescriptionDto;
    MedicineRoutineCheckDto medicineRoutineCheckDto;
    MedicineRoutineCheck medicineRoutineCheck;

    static List<Routine> routines;

    @BeforeEach
    void beforeEach() {

        member = Member.builder()
                .providerName("google")
                .nickname("박길동")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(2, 0))
                .build();
        member = memberRepository.save(member);

        // 약 1
        routines = List.of(Routine.BED_AFTER, Routine.LUNCH_AFTER, Routine.DINNER_AFTER);
        
        prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .iotLocation(4)
                .medicineDose(1)
                .unit("정")
                .routines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());


        routines = List.of(Routine.BED_BEFORE, Routine.LUNCH_AFTER, Routine.DINNER_BEFORE);

        // 약 2
        prescriptionDto = PrescriptionDto.builder()
                .customName("혈압약")
                .icd("RS-12345678")
                .krName("고혈압 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .iotLocation(3)
                .medicineDose(1)
                .unit("정")
                .routines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());


    }

    @DisplayName("Scheduling")
    @Test
    @Transactional
    void scheduleMedicineRoutineCheck() {
        // given

        // when
        medicineRoutineCheckService.scheduleMedicineRoutineCheck();

        // then
        List<Prescription> allByMemberIdBetweenDate = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now());
        Integer routinesCnt = allByMemberIdBetweenDate.stream().map(prescription1 -> prescription1.getPrescriptionMedicineRoutines().size()).reduce(0, (a, b) -> a + b);
        Assertions.assertThat(routinesCnt).isEqualTo(medicineRoutineCheckRepository.findByMemberAndDate(member, LocalDate.now()).size());

    }

    @DisplayName("MedicineRoutine Check")
    @Test
    @Transactional
    void updateMedicineRoutineCheck() {
        // given

        // when
        medicineRoutineCheckService.scheduleMedicineRoutineCheck(); // 스케줄링

        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(testPrescription.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // toggle

        // then
        Assertions.assertThat(true).isEqualTo(medicineRoutineCheckRepository.findByIdAndMemberAndDate(testPrescription.getId(), member, LocalDate.now())
                        .orElseThrow(() -> new NoSuchElementException("해당 복약에 대한 체크가 존재하지 않습니다."))
                        .getTook()
                );
    }
}