package now.eyak.routine.service;

import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.dto.request.MedicineRoutineCheckIdDto;
import now.eyak.routine.dto.request.MedicineRoutineCheckUpdateDto;
import now.eyak.routine.dto.response.MedicineRoutineCheckIdResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineDateResponseDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthDateDto;
import now.eyak.routine.dto.response.MedicineRoutineMonthResponseDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface MedicineRoutineCheckService {
    void scheduleMedicineRoutineCheck(); // 스케줄링
    MedicineRoutineCheck updateMedicineRoutineCheck(MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto, Long memberId); // 복용 기록 및 수정

    MedicineRoutineMonthDateDto getDateResultsByDateAndMember(LocalDate date, Long memberId); // 하루 단위 복용 조회
    List<MedicineRoutineMonthDateDto> getMonthResultsByMonthAndMember(YearMonth yearMonth, Long memberId); // 한달 단위 조회

    MedicineRoutineDateResponseDto getDateDetailResultsByDateAndMember(LocalDate date, Long memberId);  // 하루 단위 복용 상세 조회

    MedicineRoutineCheckIdResponseDto getMedicineRoutineCheckId(MedicineRoutineCheckIdDto medicineRoutineCheckIdDto, Long memberId);  // MedicineRoutineCheck id 조회

}
