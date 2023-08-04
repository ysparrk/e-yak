package now.eyak.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.member.domain.Member;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class SignUpDto {
    private String providerName; // naver, kakao, google
    private String token; // Authorization Server(google, naver, kakao) 에서 발급받은 token
    private String nickname;
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;

    public Member toEntity() {
        return Member.builder()
                .providerName(providerName)
                .nickname(nickname)
                .wakeTime(wakeTime)
                .breakfastTime(breakfastTime)
                .lunchTime(lunchTime)
                .dinnerTime(dinnerTime)
                .bedTime(bedTime)
                .eatingDuration(eatingDuration)
                .build();
    }
}
