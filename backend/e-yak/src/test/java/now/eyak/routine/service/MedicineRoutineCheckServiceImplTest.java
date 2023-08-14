package now.eyak.routine.service;

import now.eyak.exception.NoPermissionException;
import now.eyak.member.domain.Member;
import now.eyak.member.repository.MemberRepository;
import now.eyak.member.service.MemberService;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import now.eyak.prescription.service.PrescriptionService;
import now.eyak.routine.dto.request.MedicineRoutineCheckIdDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.dto.response.MedicineRoutineCheckIdResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineDateResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthDateDto;
import now.eyak.routine.enumeration.Routine;
import now.eyak.routine.repository.MedicineRoutineCheckRepository;
import now.eyak.routine.repository.MedicineRoutineRepository;
import now.eyak.social.Scope;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;
import now.eyak.social.service.FollowRequestService;
import now.eyak.survey.domain.ContentTextResult;
import now.eyak.survey.domain.SurveyContent;
import now.eyak.survey.dto.request.ContentTextResultDto;
import now.eyak.survey.enumeration.SurveyContentType;
import now.eyak.survey.repository.SurveyContentRepository;
import now.eyak.survey.repository.SurveyRepository;
import now.eyak.survey.service.ContentTextResultService;
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
import java.time.YearMonth;
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
    ContentTextResultService contentTextResultService;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    PrescriptionService prescriptionService;
    @Autowired
    MedicineRoutineCheckRepository medicineRoutineCheckRepository;
    @Autowired
    MedicineRoutineRepository medicineRoutineRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyContentRepository surveyContentRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    FollowRequestService followRequestService;

    Member member;
    Prescription prescription;
    PrescriptionDto prescriptionDto;
    SurveyContent surveyContent;
    ContentTextResultDto contentTextResultDto;

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
                .endDateTime(LocalDateTime.now())
                .iotLocation(4)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());


        routines = List.of(Routine.BED_AFTER, Routine.LUNCH_AFTER, Routine.DINNER_BEFORE);

        // 약 2
        prescriptionDto = PrescriptionDto.builder()
                .customName("혈압약")
                .icd("RS-12345678")
                .krName("고혈압 증상")
                .engName("some english")
                .startDateTime(LocalDateTime.now().minusDays(2))
                .endDateTime(LocalDateTime.now().plusDays(3))
                .iotLocation(3)
                .medicineDose(1.5f)
                .unit("정")
                .medicineRoutines(routines)
                .build();

        prescription = prescriptionService.insert(prescriptionDto, member.getId());
    }

    @DisplayName("Routine Check")
    @Test
    @Transactional
    void updateMedicineRoutineCheck() {
        // given
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        // when

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());

        // then
        Assertions.assertThat(true).isEqualTo(medicineRoutineCheckRepository.findByIdAndMemberAndDate(medicineRoutineCheckUpdateDto.getId(), member, LocalDate.now())
                        .orElseThrow(() -> new NoSuchElementException("해당 복약에 대한 체크가 존재하지 않습니다."))
                        .getTook()
                );
    }

    @DisplayName("Date to Month")
    @Test
    @Transactional
    void getDateResultsByDateAndMember() {
        // given
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        // when

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // true 값 하나 만들기

        MedicineRoutineMonthDateDto dateResults = medicineRoutineCheckService.getDateResultsByDateAndMember(LocalDate.now(), member.getId());

        // then
        // TODO: Assertion 작성

    }

    @DisplayName("Month")
    @Test
    @Transactional
    void getMonthResultsByMonthAndMember() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId());  // true 값 하나 만들기
        List<MedicineRoutineMonthDateDto> monthResult = medicineRoutineCheckService.getMonthResultsByMonthAndMember(YearMonth.now(), member.getId(), null); // 데이터 모으기

        // then
        Assertions.assertThat(monthResult).size().isEqualTo(YearMonth.now().lengthOfMonth());

    }

    @DisplayName("Date Detail")
    @Test
    @Transactional
    void getDateDetailResultsByDateAndMember() {
        // given

        // when
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId()); // true까지 표시

        surveyContent = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().orElseThrow(() -> new NoSuchElementException("해당날짜에 TEXT 문항 설문이 존재하지 않습니다."));


        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());

        MedicineRoutineDateResponseDto detailResult = medicineRoutineCheckService.getDateDetailResultsByDateAndMember(LocalDate.now(), member.getId(), null); // 결과 불러오기

        // then
        Assertions.assertThat(contentTextResult.getText()).isEqualTo(detailResult.getSurveyContentDtos().getContentTextResultResponse().getText());
    }

    @DisplayName("팔로우 관계인 상대방의 Date Detail 조회 - Scope ALL")
    @Test
    @Transactional
    void getDateDetailResultsByDateAndMemberRequesteeScopeALL() {
        // given
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId()); // true까지 표시

        surveyContent = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().orElseThrow(() -> new NoSuchElementException("해당날짜에 TEXT 문항 설문이 존재하지 않습니다."));


        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());

        // A와 B를 팔로우 관계 설정
        Member memberB = Member.builder()
                .providerName("google")
                .nickname("도우너")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(1, 0))
                .build();
        memberB = memberRepository.save(memberB);
        
        FollowRequestDto followRequestDto = FollowRequestDto.builder()
                .followeeNickname(member.getNickname())
                .customName(member.getNickname() +  "에 대한 별칭")
                .followerScope(Scope.CALENDAR)
                .build();

        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberB.getId());

        FollowRequestAcceptDto followRequestAcceptDto = FollowRequestAcceptDto.builder()
                .customName(memberB.getNickname() + "에 대한 별칭")
                .followeeScope(Scope.ALL)
                .build();

        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequest.getId(), member.getId());

        // when
        MedicineRoutineDateResponseDto detailResult = medicineRoutineCheckService.getDateDetailResultsByDateAndMember(LocalDate.now(), memberB.getId(), member.getId()); // memberB가 member의 하루 복약 상세 정보 조회를 요청

        // then
        Assertions.assertThat(contentTextResult.getText()).isEqualTo(detailResult.getSurveyContentDtos().getContentTextResultResponse().getText());
    }

    @DisplayName("팔로우 관계인 상대방의 Date Detail 조회 - Scope CALENDAR")
    @Test
    @Transactional
    void getDateDetailResultsByDateAndMemberRequesteeScopeCalendar() {
        // given
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .build();


        MedicineRoutineCheckIdResponseDto checkIdResponseDto = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto = MedicineRoutineCheckUpdateDto.builder()
                .id(checkIdResponseDto.getId())
                .date(LocalDate.now())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(2).getMedicineRoutine().getRoutine())
                .memberId(member.getId())
                .took(true)
                .prescriptionId(testPrescription.getId())
                .build();

        medicineRoutineCheckService.updateMedicineRoutineCheck(medicineRoutineCheckUpdateDto,member.getId()); // true까지 표시

        surveyContent = surveyContentRepository.findAllSurveyContentByDate(LocalDate.now()).stream().filter(element -> element.getSurveyContentType().equals(SurveyContentType.TEXT)).findAny().orElseThrow(() -> new NoSuchElementException("해당날짜에 TEXT 문항 설문이 존재하지 않습니다."));


        contentTextResultDto = ContentTextResultDto.builder()
                .text("오늘의 컨디션 입력합니다.")
                .build();

        ContentTextResult contentTextResult = contentTextResultService.saveTextSurveyResult(contentTextResultDto, surveyContent.getId(), member.getId());

        // A와 B를 팔로우 관계 설정
        Member memberB = Member.builder()
                .providerName("google")
                .nickname("도우너")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(1, 0))
                .build();
        memberB = memberRepository.save(memberB);
        Long memberBId = memberB.getId();

        FollowRequestDto followRequestDto = FollowRequestDto.builder()
                .followeeNickname(member.getNickname())
                .customName(member.getNickname() +  "에 대한 별칭")
                .followerScope(Scope.CALENDAR)
                .build();

        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberB.getId());

        FollowRequestAcceptDto followRequestAcceptDto = FollowRequestAcceptDto.builder()
                .customName(memberB.getNickname() + "에 대한 별칭")
                .followeeScope(Scope.CALENDAR)
                .build();

        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequest.getId(), member.getId());

        // when
        // then
        Assertions.assertThatThrownBy(() -> medicineRoutineCheckService.getDateDetailResultsByDateAndMember(LocalDate.now(), memberBId, member.getId()))
                .isInstanceOf(NoPermissionException.class); // memberB가 member의 하루 복약 상세 정보 조회를 요청시 Scope이 CALENDAR이므로 예외 발생
    }

    @DisplayName("Get MedicineRoutineCheckId")
    @Test
    @Transactional
    void getMedicineRoutineCheckId() {
        // given
        Prescription testPrescription = prescriptionService.findAllByMemberIdBetweenDate(member.getId(), LocalDateTime.now()).get(1);

        MedicineRoutineCheckIdDto medicineRoutineCheckIdDto = MedicineRoutineCheckIdDto.builder()
                .date(LocalDate.now())
                .prescriptionId(testPrescription.getId())
                .routine(testPrescription.getPrescriptionMedicineRoutines().get(1).getMedicineRoutine().getRoutine())
                .build();

        // when
        MedicineRoutineCheckIdResponseDto medicineRoutineCheckId = medicineRoutineCheckService.getMedicineRoutineCheckId(medicineRoutineCheckIdDto, member.getId());

        // then
        System.out.println("medicineRoutineCheckId = " + medicineRoutineCheckId);
        // TODO: Assertions 작성

    }
}