package now.eyak.prescription.repository;

import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByMember(Member member);
    List<Prescription> findAllByMemberAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(Member member, LocalDateTime dateTimeA, LocalDateTime dateTimeB);
    Optional<Prescription> findByIdAndMember(Long id, Member member);
    void deleteByIdAndMember(Long id, Member member);
}
