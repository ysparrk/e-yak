package now.eyak.prescription.service;

import lombok.RequiredArgsConstructor;
import now.eyak.exception.NoPermissionException;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.prescription.domain.Prescription;
import now.eyak.prescription.dto.PrescriptionDto;
import now.eyak.prescription.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Prescription insert(PrescriptionDto prescriptionDto, Long memberId) {
        Prescription prescription = prescriptionDto.toPrescription();
        Member member = getMemberOrThrow(memberId);
        prescription.setMember(member);

        return prescriptionRepository.save(prescription);
    }

    @Transactional
    @Override
    public List<Prescription> findAllByMemberId(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return prescriptionRepository.findAllByMember(member);
    }

    @Transactional
    @Override
    public Prescription findById(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    @Transactional
    @Override
    public Prescription update(Long prescriptionId, PrescriptionDto prescriptionDto, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Prescription prescription = getPrescriptionAndCheckPermission(prescriptionId, member);

        prescription = prescriptionDto.update(prescription);

        return prescriptionRepository.save(prescription);
    }

    @Transactional
    @Override
    public void delete(Long prescriptionId, Long memberId) {
        Member member = getMemberOrThrow(memberId);

        prescriptionRepository.deleteByIdAndMember(prescriptionId, member);
    }

    private Prescription getPrescriptionAndCheckPermission(Long prescriptionId, Member member) {
        Prescription prescription = getPrescriptionByIdAndMemberOrThrow(prescriptionId, member);

        return prescription;
    }

    private Prescription getPrescriptionByIdAndMemberOrThrow(Long prescriptionId, Member member) {
        Prescription prescription = prescriptionRepository.findByIdAndMember(prescriptionId, member).orElseThrow(() -> new NoSuchElementException("회원에 대해서 해당 Prescription은 존재하지 않습니다."));
        return prescription;
    }

    private Member getMemberOrThrow(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다."));
        return member;
    }


}
