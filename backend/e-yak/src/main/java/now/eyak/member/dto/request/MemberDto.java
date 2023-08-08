package now.eyak.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.member.domain.Member;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class MemberDto {
    private Long id;
    private String providerName; // ex) google, naver, kakao
    private String refreshToken;
    private String nickname;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;

    @Builder
    public MemberDto(Long id, String providerName, String refreshToken, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt, LocalTime wakeTime, LocalTime breakfastTime, LocalTime lunchTime, LocalTime dinnerTime, LocalTime bedTime, LocalTime eatingDuration) {
        this.id = id;
        this.providerName = providerName;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.wakeTime = wakeTime;
        this.breakfastTime = breakfastTime;
        this.lunchTime = lunchTime;
        this.dinnerTime = dinnerTime;
        this.bedTime = bedTime;
        this.eatingDuration = eatingDuration;
    }

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .providerName(member.getProviderName())
                .refreshToken(member.getRefreshToken())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .wakeTime(member.getWakeTime())
                .breakfastTime(member.getBreakfastTime())
                .lunchTime(member.getLunchTime())
                .dinnerTime(member.getDinnerTime())
                .bedTime(member.getBedTime())
                .eatingDuration(member.getEatingDuration())
                .build();
    }
}
