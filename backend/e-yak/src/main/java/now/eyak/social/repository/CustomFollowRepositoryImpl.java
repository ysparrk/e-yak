package now.eyak.social.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.social.domain.QFollow;
import now.eyak.social.dto.FollowerResponseDto;

@RequiredArgsConstructor
public class CustomFollowRepositoryImpl implements CustomFollowRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
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
                .on(followA.follower.id.eq(followB.followee.id))
                .where(followA.followee.id.eq(followee.getId()))
                .fetch();

        return followerResponseDtoList;
    }
}
