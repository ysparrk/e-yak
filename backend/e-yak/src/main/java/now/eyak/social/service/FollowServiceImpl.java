package now.eyak.social.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.social.domain.Follow;
import now.eyak.social.dto.FollowUpdateDto;
import now.eyak.social.dto.FollowerResponseDto;
import now.eyak.social.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자(memberId)의 팔로워들을 전체 조회한다.
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public List<FollowerResponseDto> findFollowers(Long memberId) {
        Member member = getMember(memberId);

        return followRepository.findFollowByFollowee(member);
    }

    /**
     * 팔로우(followId)를 취소한다.
     *
     * A -> B 를 취소한다고 하면
     * A -> B
     * B <- A 두 개의 팔로우 row가 삭제된다.
     * @param followId
     * @param memberId
     */
    @Transactional
    @Override
    public void deleteFollowBi(Long followId, Long memberId) {
        log.info("7777followId" + followId);
        log.info("7777memberId" + memberId);

        Member followee = getMember(memberId);
        Follow follow = followRepository.findByIdAndFollowee(followId, followee).orElseThrow(() -> new NoSuchElementException("해당하는 Follow가 존재하지 않습니다."));

        Member follower = follow.getFollower();
        Follow inverseFollow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new NoSuchElementException("해당하는 Follow가 존재하지 않습니다."));

        followRepository.delete(inverseFollow);
        followRepository.delete(follow);
    }

    /**
     * 팔로우 관계를 맺은 사용자에 대한 별칭과 공개 범위를 수정한다.
     *
     * @param followUpdateDto
     * @param followId
     * @param memberId
     * @return
     */
    @Transactional
    @Override
    public Follow updateFollow(FollowUpdateDto followUpdateDto, Long followId, Long memberId) {
        Member member = getMember(memberId);
        Follow follow = followRepository.findByIdAndFollowee(followId, member).orElseThrow(() -> new NoSuchElementException("해당하는 Follow가 존재하지 않습니다."));

        follow.setFolloweeScope(followUpdateDto.getScope());
        follow.setCustomName(followUpdateDto.getCustomName());

        return followRepository.save(follow);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 존재하지 않습니다."));
    }
}
