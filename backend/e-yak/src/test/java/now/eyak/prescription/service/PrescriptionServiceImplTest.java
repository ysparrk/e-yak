package now.eyak.prescription.service;

import now.eyak.member.domain.Member;
import now.eyak.member.domain.enumeration.Role;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.routine.domain.PrescriptionMedicineRoutine;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PrescriptionServiceImplTest {

    @Autowired
    PrescriptionService prescriptionService;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    MedicineRoutineRepository medicineRoutineRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MedicineRoutineCheckRepository medicineRoutineCheckRepository;

    static Member MEMBER;

    static List<Routine> routines;

    @BeforeEach
    void beforeEach() {
        Member member = Member.builder()
                .providerId("google")
                .providerId("google_something")
                .refreshToken("refreshToken")
                .nickname("박길동")
                .role(Role.USER)
                .wakeTime(LocalTime.of(7, 0))
                .breakfastTime(LocalTime.of(8, 0))
                .lunchTime(LocalTime.of(14, 0))
                .dinnerTime(LocalTime.of(18, 0))
                .bedTime(LocalTime.of(22, 0))
                .eatingDuration(LocalTime.of(0, 30))
                .build();

        MEMBER = memberRepository.save(member);

        routines = List.of(Routine.BREAKFAST_AFTER, Routine.LUNCH_AFTER, Routine.DINNER_AFTER);
    }

    @Transactional
    @Test
    void insert() {
        //given

        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        //when
        Prescription prescription = prescriptionService.insert(prescriptionDto, MEMBER.getId());
        //then
        Prescription expected = prescriptionDto.toEntity();

        // TODO: 테스트 코드 작성에 용이하게 구조 변경
        assertThat(prescription.getCustomName()).isEqualTo(expected.getCustomName());
        // TODO: 복약 체크 관련 Assertions 작성
    }

    @Transactional
    @Test
    void findAllByMemberId() {
        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        //when
        prescriptionService.insert(prescriptionDto, MEMBER.getId());
        prescriptionService.insert(prescriptionDto, MEMBER.getId());
        prescriptionService.insert(prescriptionDto, MEMBER.getId());



        List<Prescription> prescriptions = prescriptionRepository.findAllByMember(MEMBER);

        assertThat(prescriptions).hasSize(3);
    }

    @Transactional
    @Test
    void findById() {
        //given
        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        Prescription inserted = prescriptionService.insert(prescriptionDto, MEMBER.getId());

        //when
        Prescription prescription = prescriptionService.findById(inserted.getId(), MEMBER.getId());

        //then
        assertThat(prescription.getId()).isEqualTo(inserted.getId());
    }

    @Transactional
    @Test
    void update() {
        //given
        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        Prescription inserted = prescriptionService.insert(prescriptionDto, MEMBER.getId());

        //when
        prescriptionDto.setCustomName("두통약");
        prescriptionDto.setMedicineRoutines(List.of(Routine.BED_BEFORE, Routine.BED_AFTER));
        prescriptionService.update(inserted.getId(), prescriptionDto, MEMBER.getId());

        Prescription prescription = prescriptionService.findById(inserted.getId(), MEMBER.getId());

        //then
        assertThat(prescription.getCustomName()).isEqualTo(prescriptionDto.getCustomName());
        assertThat(prescription.getPrescriptionMedicineRoutines()).hasSize(prescriptionDto.getMedicineRoutines().size());
    }

    @Transactional
    @Test
    void delete() {
        //given
        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        Prescription inserted = prescriptionService.insert(prescriptionDto, MEMBER.getId());

        //when
        prescriptionService.delete(inserted.getId(), MEMBER.getId());

        //then
        assertThat(prescriptionService.findAllByMemberId(MEMBER.getId())).isEmpty();
        assertThatThrownBy(() -> prescriptionService.findPrescriptionMedicineRoutinesById(inserted.getId(), MEMBER.getId())).isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Transactional
    @Test
    void findPrescriptionMedicineRoutinesById() {
        //given
        PrescriptionDto prescriptionDto = PrescriptionDto.builder()
                .customName("감기약")
                .icd("RS-1203123")
                .krName("감기바이러스에 의한 고열 및 인후통 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        Prescription inserted = prescriptionService.insert(prescriptionDto, MEMBER.getId());

        //when
        List<PrescriptionMedicineRoutine> prescriptionMedicineRoutines = prescriptionService.findPrescriptionMedicineRoutinesById(inserted.getId(), MEMBER.getId());

        //then
        assertThat(prescriptionMedicineRoutines).hasSize(prescriptionDto.getMedicineRoutines().size());
    }
}