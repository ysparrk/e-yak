package now.eyak.member.domain;

import jakarta.persistence.*;
import lombok.*;
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
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerName; // ex) google, naver, kakao
    private String providerId; // "google_" + Google, Naver, Kakao에서 로그인시 전달되는 id
    private String refreshToken;
    @Column(unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;
    private Boolean agreement = true;

    @OneToMany(mappedBy = "member")
    private List<Prescription> prescriptions = new ArrayList<>();

    @Builder
    public Member(String providerName, String providerId, String refreshToken, String nickname, Role role, LocalTime wakeTime, LocalTime breakfastTime, LocalTime lunchTime, LocalTime dinnerTime, LocalTime bedTime, LocalTime eatingDuration) {
        this.providerName = providerName;
        this.providerId = providerId;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.role = role;
        this.wakeTime = wakeTime;
        this.breakfastTime = breakfastTime;
        this.lunchTime = lunchTime;
        this.dinnerTime = dinnerTime;
        this.bedTime = bedTime;
        this.eatingDuration = eatingDuration;
    }
}
