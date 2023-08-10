package now.eyak.social.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import now.eyak.member.domain.Member;
import now.eyak.social.dto.FollowResponseDto;

import java.util.List;

@RequiredArgsConstructor
public class CustomFollowRepositoryImpl implements CustomFollowRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FollowResponseDto> findFollowByFollowee(Member followee) {
//        jpaQueryFactory
//                .select()
        return null;
    }
}
