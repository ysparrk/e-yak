package now.eyak.social.service;

import now.eyak.member.domain.Member;
import now.eyak.member.domain.enumeration.Role;
import now.eyak.member.repository.MemberRepository;
import now.eyak.social.Scope;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;
import now.eyak.social.dto.FollowerResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class FollowServiceImplTest {
    @Autowired
    FollowService followService;
    @Autowired
    FollowRequestService followRequestService;
    @Autowired
    MemberRepository memberRepository;

    @Transactional
    @Test
    void findFollowers() {
        //given
        Member memberA = Member.builder()
                .nickname("홍길동")
                .role(Role.USER)
                .build();

        memberRepository.save(memberA);

        Member memberB = Member.builder()
                .nickname("도우너")
                .role(Role.USER)
                .build();

        memberRepository.save(memberB);

        FollowRequestDto followRequestDto = FollowRequestDto.builder()
                .followeeNickname(memberB.getNickname())
                .customName("도우너에 대한 별칭")
                .followerScope(Scope.ALL)
                .build();

        FollowRequest followRequest = followRequestService.insertFollowRequest(followRequestDto, memberA.getId());

        FollowRequestAcceptDto followRequestAcceptDto = FollowRequestAcceptDto.builder()
                .customName("홍길동에 대한 별칭")
                .followeeScope(Scope.ALL)
                .build();

        followRequestService.acceptFollowRequest(followRequestAcceptDto, followRequest.getId(), memberB.getId());

        //when
        List<FollowerResponseDto> followers = followService.findFollowers(memberB.getId());

        //then
        System.out.println("followers.get(0).getCustomName() = " + followers.get(0).getCustomName());
        Assertions.assertThat(followers.get(0).getCustomName()).isEqualTo(followRequestAcceptDto.getCustomName());

    }
}