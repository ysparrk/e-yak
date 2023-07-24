package now.eyak.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.enumeration.Role;
import now.eyak.prescription.domain.Prescription;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerName; // ex) google, naver, kakao
    private String providerId; // "google_" + Google, Naver, Kakao에서 로그인시 전달되는 id
    private String refreshToken;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

//    private String phoneNumber; // TODO: 수집 정보 결정
//    private String baseAddress;
//    private String detailAddress;
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;
//    private String specifics; // 특이사항 줄 글로
//    private String supplements; // 영양제등을 줄 글로
    @OneToMany(mappedBy = "member")
    private List<Prescription> prescriptions = new ArrayList<>();

    @Builder
    public Member(String providerName, String providerId) {
        this.providerName = providerName;
        this.providerId = providerId;
    }
}
