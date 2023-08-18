package now.eyak.prescription.repository;

import now.eyak.member.domain.Member;
import now.eyak.prescription.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long>, CustomPrescriptionRepository {
    void deleteByIdAndMember(Long id, Member member);
}
