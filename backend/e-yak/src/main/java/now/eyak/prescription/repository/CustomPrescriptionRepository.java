package now.eyak.prescription.repository;

import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.query.PrescriptionListQueryDto;
import now.eyak.prescription.dto.query.PrescriptionRoutineFutureQueryDto;
import now.eyak.prescription.dto.query.PrescriptionRoutineQueryDto;
import now.eyak.routine.enumeration.Routine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomPrescriptionRepository {
    List<Prescription> findAllByMemberAndBetweenStartAndEndDateTime(Member member, LocalDateTime dateTime); // 요청받은 날짜에 복용중인 약인지 확인

    List<PrescriptionListQueryDto> findAllByMemberAndBetweenDates(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime); // 요청받은 날짜의 기간에 복용중인 약 리스트

    List<PrescriptionRoutineQueryDto> findByRoutine(Routine routine, Member member, LocalDateTime dateTime);

    List<PrescriptionRoutineFutureQueryDto> findByRoutineForFuture(Routine routine, Member member, LocalDateTime dateTime); // 요청받은 날짜의 기간에 복용중인 약 리스트(미래)

    Optional<Prescription> findByIdAndMember(Long id, Member member);

    List<Prescription> findAllByMember(Member member);
}
