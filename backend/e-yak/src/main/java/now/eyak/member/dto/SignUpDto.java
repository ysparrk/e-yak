package now.eyak.member.dto;

import lombok.Getter;
import now.eyak.member.domain.Member;

import java.time.LocalTime;

@Getter
public class SignUpDto {
    private String providerName; // naver, kakao, google
    private String token; // Authentication Server 에서 발급받은 token
    private String nickname;
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;

    public Member setMemberFields(Member member) {
        member.setNickname(this.nickname);
        member.setWakeTime(this.wakeTime);
        member.setBreakfastTime(this.breakfastTime);
        member.setLunchTime(this.lunchTime);
        member.setDinnerTime(this.dinnerTime);
        member.setBedTime(this.bedTime);
        member.setEatingDuration(this.eatingDuration);

        return member;
    }
}
