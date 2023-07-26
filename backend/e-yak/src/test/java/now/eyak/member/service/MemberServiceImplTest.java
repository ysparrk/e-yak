package now.eyak.member.service;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.MemberDto;
import now.eyak.member.dto.MemberUpdateDto;
import now.eyak.member.dto.SignUpDto;
import now.eyak.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    void updateMember() {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .providerName("google")
                .nickname("박길동")
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.NOON)
                .dinnerTime(LocalTime.now())
                .bedTime(LocalTime.MIDNIGHT)
                .eatingDuration(LocalTime.of(2, 0))
                .build();

        Member member = new Member();
        member = signUpDto.setMemberFields(member);

        member.setProviderId("google_something");

        memberRepository.save(member);

        //when
        LocalTime updatedTime = LocalTime.of(18, 0);
        MemberUpdateDto memberUpdateDto = MemberUpdateDto.builder()
                .wakeTime(LocalTime.MIN)
                .breakfastTime(LocalTime.MIN)
                .lunchTime(LocalTime.MIN)
                .dinnerTime(LocalTime.MIN)
                .bedTime(LocalTime.MIN)
                .eatingDuration(updatedTime)
                .build();

        memberService.updateMember(memberUpdateDto, member.getId());
        Member actual = memberRepository.findById(member.getId()).get();

        //then
        Assertions.assertThat(actual.getEatingDuration()).isEqualToIgnoringNanos(updatedTime);
    }
}