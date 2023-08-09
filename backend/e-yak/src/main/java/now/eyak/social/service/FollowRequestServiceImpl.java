package now.eyak.social.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.exception.NoPermissionException;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import now.eyak.social.domain.Follow;
import now.eyak.social.domain.FollowRequest;
import now.eyak.social.dto.FollowRequestAcceptDto;
import now.eyak.social.dto.FollowRequestDto;
import now.eyak.social.exception.AlreadyFollowedException;
import now.eyak.social.exception.BiDirectionalFollowRequestException;
import now.eyak.social.repository.FollowRepository;
import now.eyak.social.repository.FollowRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowRequestServiceImpl implements FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * 팔로워(followerId)가 다른 사용자(followeeId)에게 팔로우 요청을 보낸다.
     * 팔로우 요청에 팔로워가 다른 사용자에게 자신의 정보를 얼마나 공개할 지 scope 정보가 포함된다.
     * followerId와 followeeId가 같은 경우는 허용되지 않는다.
     *
     * @param followRequestDto followeeId, scope
     * @param followerId       팔로워 ID
     * @return
     */
    @Transactional
    @Override
    public FollowRequest insertFollowRequest(FollowRequestDto followRequestDto, Long followerId) {
        String followeeNickname = followRequestDto.getFolloweeNickname();

        Member followee = memberRepository.findByNickname(followeeNickname).orElseThrow(() -> new NoSuchMemberException("닉네임과 일치하는 회원이 존재하지 않습니다."));
        Member follower = getFollower(followerId);

        if (Objects.equals(followee.getId(), follower.getId())) {
            throw new IllegalArgumentException("자기자신에게 팔로우 요청을 보낼 수 없습니다.");
        }

        if (followRequestRepository.findByFollowerAndFollowee(followee, follower).isPresent() || followRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            throw new BiDirectionalFollowRequestException("이미 " + followee.getNickname() + "가 " + follower.getNickname() + "에게 팔로우 요청을 보낸 상태입니다.");
        };

        if (followRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            throw new AlreadyFollowedException("이미 팔로우 된 사이입니다.");
        }

        FollowRequest followRequest = followRequestRepository.findByFollowerAndFollowee(follower, followee).orElse(
                FollowRequest.builder()
                        .followee(followee)
                        .follower(follower)
                        .scope(followRequestDto.getFollowerScope())
                        .customName(followRequestDto.getCustomName())
                        .build()
        );

        followRequestDto.update(followRequest);

        return followRequestRepository.save(followRequest);
    }

    /**
     * 팔로우 요청을 요청자(requester)가 취소하거나 피팔로우 사용자(followee)가 팔로우 요청을 거절한다.
     * @param followRequestId
     * @param memberId 요청자 ID(requesterId)
     */
    @Transactional
    @Override
    public void declineOrCancelFollowRequest(Long followRequestId, Long memberId) {
        FollowRequest followRequest = getFollowRequest(followRequestId);

        Member followee = followRequest.getFollowee();
        Member follower = followRequest.getFollower();
        Member requester = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당하는 회원 정보가 없습니다."));

        if (!(requester.getId().equals(followee.getId()) || requester.getId().equals(follower.getId()))) {
            throw new NoPermissionException("해당 Follow Request에 대한 삭제 권한이 없습니다.");
        }

        followRequestRepository.delete(followRequest);
    }

    /**
     * 피팔로우 사용자(followeeId)가 팔로우 요청(followRequestId)를 수락한다.
     * 피팔로우 사용자는 자신의 정보 공개 범위를 scope 변수를 통해 설정한다.
     *
     * A 가 B 의 요청을 수락하면, Follow 테이블에 2개의 row를 삽입한다.
     *
     * follower followee followeeScope
     * A        B       B_scope
     * B        A       A_scope
     *
     * @param followRequestId
     * @param followeeId
     * @return 순방향 follow 객체
     */
    @Transactional
    @Override
    public Follow acceptFollowRequest(FollowRequestAcceptDto followRequestAcceptDto, Long followRequestId, Long followeeId) {
        FollowRequest followRequest = getFollowRequest(followRequestId);

        if (!followRequest.getFollowee().getId().equals(followeeId)) {
            throw new NoPermissionException("해당 Follow Request에 대한 수락 권한이 없습니다.");
        }

        Follow follow = Follow.builder()
                .followee(followRequest.getFollowee())
                .follower(followRequest.getFollower())
                .followeeScope(followRequestAcceptDto.getFolloweeScope())
                .customName(followRequest.getCustomName())
                .build();

        Follow followInverse = Follow.builder()
                .follower(followRequest.getFollowee())
                .followee(followRequest.getFollower())
                .followeeScope(followRequest.getScope())
                .customName(followRequestAcceptDto.getCustomName())
                .build();

        followRequestRepository.delete(followRequest);

        followRepository.save(follow);
        followRepository.save(followInverse);

        return follow;
    }

    /**
     * 팔로잉 사용자(FollowerId)가 요청한 현재 팔로우 요청 전체를 반환한다.
     *
     * @param followerId
     * @return
     */
    @Override
    public List<FollowRequest> retrieveAllFollowRequestByFollowerId(Long followerId) {
        Member follower = getFollower(followerId);

        return followRequestRepository.findByFollower(follower);
    }

    /**
     * 피팔로잉 사용자(FollowerId)에게 요청된 현재 팔로우 요청 전체를 반환한다.
     *
     * @param followeeId
     * @return
     */
    @Override
    public List<FollowRequest> retrieveAllFollowRequestByFolloweeId(Long followeeId) {
        Member followee = getFollowee(followeeId);

        return followRequestRepository.findByFollowee(followee);
    }

    private FollowRequest getFollowRequest(Long followRequestId) {
        return followRequestRepository.findById(followRequestId).orElseThrow(() -> new NoSuchElementException("해당하는 Follow Request가 존재하지 않습니다."));
    }

    private Member getFollower(Long followerId) {
        return memberRepository.findById(followerId).orElseThrow(() -> new NoSuchMemberException("해당하는 follower 정보가 없습니다"));
    }

    private Member getFollowee(Long followerId) {
        return memberRepository.findById(followerId).orElseThrow(() -> new NoSuchMemberException("해당하는 followee 정보가 없습니다."));
    }
}
