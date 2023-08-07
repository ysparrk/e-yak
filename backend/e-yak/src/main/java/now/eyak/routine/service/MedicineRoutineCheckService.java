package now.eyak.routine.service;

import now.eyak.routine.domain.MedicineRoutineCheck;
import now.eyak.routine.dto.*;

import java.time.LocalDate;
import java.time.YearMonth;

public interface MedicineRoutineCheckService {
    void scheduleMedicineRoutineCheck(); // 스케줄링
    MedicineRoutineCheck updateMedicineRoutineCheck(MedicineRoutineCheckUpdateDto medicineRoutineCheckUpdateDto, Long memberId); // 복용 기록 및 수정

    MedicineRoutineMonthDateDto getDateResultsByDateAndMember(LocalDate date, Long memberId); // 하루 단위 복용 조회
    MedicineRoutineMonthResponseDto getMonthResultsByMonthAndMember(YearMonth yearMonth, Long memberId); // 한달 단위 조회

    MedicineRoutineDateResponseDto getDateDetailResultsByDateAndMember(LocalDate date, Long memberId);  // 하루 단위 복용 상세 조회


}
