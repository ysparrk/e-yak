package now.eyak.social.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.social.domain.QFollow;
import now.eyak.social.dto.FollowerResponseDto;

import java.util.List;

@RequiredArgsConstructor
public class CustomFollowRepositoryImpl implements CustomFollowRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FollowerResponseDto> findFollowByFollowee(Member followee) {
        QFollow followA = new QFollow("followA");
        QFollow followB = new QFollow("followB");

        List<FollowerResponseDto> followerResponseDtoList = jpaQueryFactory
                .select(Projections.constructor(FollowerResponseDto.class,
                        followA.id,
                        followA.follower.id,
                        followA.follower.nickname,
                        followB.customName,
                        followA.followeeScope
                )).from(followA)
                .join(followB)
                .on(followA.follower.eq(followB.followee))
                .where(followA.followee.eq(followee))
                .fetch();

        return followerResponseDtoList;
    }
}
