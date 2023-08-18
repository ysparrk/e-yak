package now.eyak.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import now.eyak.member.domain.Member;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class MemberUpdateDto {
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;

    public Member update(Member member) {
        member.setWakeTime(wakeTime);
        member.setBreakfastTime(breakfastTime);
        member.setLunchTime(lunchTime);
        member.setDinnerTime(dinnerTime);
        member.setBedTime(bedTime);
        member.setEatingDuration(eatingDuration);

        return member;
    }
}
