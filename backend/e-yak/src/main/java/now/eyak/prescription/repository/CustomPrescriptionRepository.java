package now.eyak.prescription.repository;

import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomPrescriptionRepository {
    List<Prescription> findAllByMemberAndBetweenStartAndEndDateTime(Member member, LocalDateTime dateTime);

}
