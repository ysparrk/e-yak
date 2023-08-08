package now.eyak.member.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.domain.OAuthProvider;
import now.eyak.member.domain.enumeration.Role;
import now.eyak.member.dto.*;
import now.eyak.member.exception.AlreadySignUpException;
import now.eyak.member.exception.InvalidRefreshTokenException;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.InMemoryProviderRepository;
import now.eyak.member.repository.MemberRepository;
import now.eyak.util.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final InMemoryProviderRepository inMemoryProviderRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    @Override
    public SignInResponseDto signIn(SignInDto signInDto) throws Exception {
        log.debug("signIn()");

        OAuthProvider provider = findProvider(signInDto.getProviderName());
        String jwksStr = provider.getJwks();
        String token = signInDto.getToken();


        SignedJWT signedJWT = (SignedJWT) jwtTokenProvider.parseTokenWithoutValidation(token);

        try {
            jwtTokenProvider.validateJwtWithJwk(token, jwksStr);
        } catch (Exception e) {
            log.info("Id Token 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("Id Token이 유효하지 않습니다.");
        }

        // 사용자 조회
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        String sub = jwtClaimsSet.getStringClaim("sub");
        String providerId = signInDto.getProviderName() + "_" + sub;

        Member member = memberRepository.findByProviderId(providerId).orElseThrow(() -> new NoSuchMemberException("회원 가입을 먼저 진행하십시오."));

        String accessToken = jwtTokenProvider.bulidAccessToken(member);
        String refreshToken = jwtTokenProvider.buildRefreshToken(member);

        member.setRefreshToken(refreshToken);

        return SignInResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberDto(MemberDto.from(member))
                .build();
    }

    /**
     * 회원가입을 진행한다. 인자로 받은 ID Token을 검증한다. 해당 유저정보가 DB에 없다면 회원가입을 진행한다. 있다면 예외를
     * 발생시킨다.
     *
     * @param signUpDto ID Token과 과 이용약관 동의 목록을 가진다.
     * @return
     */
    @Transactional
    @Override
    public Member signUp(SignUpDto signUpDto) throws Exception {
        OAuthProvider provider = findProvider(signUpDto.getProviderName());
        String jwksStr = provider.getJwks();
        String token = signUpDto.getToken();

        SignedJWT signedJWT = (SignedJWT) jwtTokenProvider.parseTokenWithoutValidation(token);

        try {
            jwtTokenProvider.validateJwtWithJwk(token, jwksStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Id Token이 유효하지 않습니다. " + e.getMessage());
        }

        // 사용자 정보를 저장
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        String sub = jwtClaimsSet.getStringClaim("sub");
        String providerId = signUpDto.getProviderName() + "_" + sub;

        memberRepository.findByProviderId(providerId).ifPresent(member -> {
            throw new AlreadySignUpException("이미 가입된 사용자입니다.");
        });

        Member member = signUpDto.toEntity();
        member.setProviderId(providerId);
        member.setRole(Role.USER);

        Member savedMember = memberRepository.save(member);

        return savedMember;
    }

    /**
     * Refresh Token으로 Access Token을 재발급한다.
     *
     * @param reissueDto
     * @return
     */
    @Transactional
    @Override
    public RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto) {
        try {
            SignedJWT signedJWT = (SignedJWT) jwtTokenProvider.parseRefreshToken(reissueDto.getRefreshToken());
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Member member = memberRepository.findById(claims.getLongClaim("id")).orElseThrow(() -> new InvalidRefreshTokenException("해당 사용자가 존재하지 않습니다."));
            if (!member.getRefreshToken().equals(reissueDto.getRefreshToken())) {
                throw new InvalidRefreshTokenException("유효하지 않은 Refresh Token 입니다.");
            }

            String refreshToken = jwtTokenProvider.buildRefreshToken(member);
            member.setRefreshToken(refreshToken);

            String accessToken = jwtTokenProvider.bulidAccessToken(member);

            return RefreshResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (ParseException|JOSEException e) {
            throw new InvalidRefreshTokenException("유효하지 않은 Refresh Token 입니다. " + e.getMessage());
        }
    }

    @Override
    public MemberDto retrieveMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당 회원은 존재하지 않습니다."));

        return MemberDto.from(member);
    }

    @Transactional
    @Override
    public MemberDto updateMember(MemberUpdateDto memberUpdateDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchMemberException("해당 회원은 존재하지 않습니다."));

        Member updatedMember = memberUpdateDto.update(member);

        return MemberDto.from(updatedMember);
    }

    @Override
    public void deleteMember(Long memberId) {
        // FK 제약 조건 고려


        memberRepository.deleteById(memberId);
    }

    @Override
    public boolean isDuplicatedNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    private OAuthProvider findProvider(String providerName) {
        return inMemoryProviderRepository.findByProviderName(providerName);
    }
}
