package now.eyak.member.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import now.eyak.member.domain.enumeration.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Member implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String providerName; // ex) google, naver, kakao
    private String providerId; // "google_" + Google, Naver, Kakao에서 로그인시 전달되는 id
    private String refreshToken;
    @NotNull
    @Column(unique = true, length = 20)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();
    @NotNull
    private LocalTime wakeTime;
    @NotNull
    private LocalTime breakfastTime;
    @NotNull
    private LocalTime lunchTime;
    @NotNull
    private LocalTime dinnerTime;
    @NotNull
    private LocalTime bedTime;
    @NotNull
    private LocalTime eatingDuration;
    private Boolean agreement = true;

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
