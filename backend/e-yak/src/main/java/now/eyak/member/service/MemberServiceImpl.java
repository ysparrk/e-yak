package now.eyak.member.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.domain.MemberProfile;
import now.eyak.member.domain.OAuthAttributes;
import now.eyak.member.domain.OAuthProvider;
import now.eyak.member.dto.*;
import now.eyak.member.exception.*;
import now.eyak.member.repository.InMemoryProviderRepository;
import now.eyak.member.repository.MemberRepository;
import now.eyak.util.jwt.JwtTokenProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final InMemoryProviderRepository inMemoryProviderRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberServiceImpl(InMemoryProviderRepository inMemoryProviderRepository, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.inMemoryProviderRepository = inMemoryProviderRepository;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * accessToken으로 authentication server에서 사용자 정보를 받아온다. DB를 검색해 해당 사용자가 있으면 이미 회원가입이 되어 있으므로 jwtToken 을
     * OAuthSignInResponseDto 에 담아 발급한다.
     *
     * @return OAuthSignInResponseDto
     */
    @Transactional
    @Override
    public SignInResponseDto signIn(SignInDto signInDto) throws InvalidAccessTokenException, UnsupportedProviderException {
        log.debug("signIn()");
        // 요청 파라미터로 들어온 Access Token의 유효성 검사를 위해 Access Token으로 Authoriaztion Server에 사용자 정보를 요청한다.
        MemberProfile memberProfile = getMemberProfile(
                signInDto.getProviderName(),
                signInDto.getToken(),
                inMemoryProviderRepository.findByProviderName(signInDto.getProviderName())
        );

        SignInResponseDto oAuthSignInResponseDto =
                memberRepository
                        .findByProviderId(memberProfile.getProviderId())
                        .map(member -> {
                                    String refreshToken = jwtTokenProvider.buildRefreshToken(member);
                                    member.setRefreshToken(refreshToken);
                            return new SignInResponseDto(jwtTokenProvider.bulidAccessToken(member), refreshToken, MemberDto.from(member));
                                }
                        )
                        .orElseThrow(() -> new NoSuchMemberException("회원 가입을 먼저 진행하십시오."));


        return oAuthSignInResponseDto;
    }

    /**
     * 회원가입을 진행한다. 인자로 받은 Access Token 으로 Authorization Server 에 유저정보를 요청한다. 해당 유저정보가 DB에 없다면 회원가입을 진행한다. 있다면 예외를
     * 발생시킨다.
     *
     * @param signUpDto Access Token 과 이용약관 동의 목록을 가진다.
     * @return OAuthSignUpResponseDto 성공을 나타내는 "success"를 가진다.
     */
    @Transactional
    @Override
    public Member signUp(SignUpDto signUpDto) {
        MemberProfile memberProfile = getMemberProfile(
                signUpDto.getProviderName(),
                signUpDto.getToken(),
                inMemoryProviderRepository.findByProviderName(signUpDto.getProviderName())
        );

        checkDuplication(memberProfile.getProviderId());

        Member member = memberProfile.toMember();
        member = signUpDto.setMemberFields(member);
        member = memberRepository.save(member);

        String refreshToken = jwtTokenProvider.buildRefreshToken(member);
        member.setRefreshToken(refreshToken);

        return member;
    }

    @Transactional
    @Override
    public RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto) {
        Claims claims = jwtTokenProvider.parseRefreshToken(reissueDto.getRefreshToken());

        Member member = memberRepository.findById(claims.get("id", Long.class)).orElseThrow(() -> new InvalidRefreshTokenException("해당 사용자가 존재하지 않습니다."));
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
    }

    private OAuthProvider findProvider(String providerName) {
        return inMemoryProviderRepository.findByProviderName(providerName);
    }

    /**
     * 사용자의 중복 가입 체크
     * 체크 방법: oAuthId가 중복되는지 체크
     * 중복 가입인 경우 예외를 던진다.
     */
    public void checkDuplication(String providerId) {
        memberRepository.findByProviderId(providerId).ifPresent(member -> {
            throw new AlreadySignUpException("이미 가입이 된 계정입니다.");
        });
    }


    /**
     * provider 의 Authorization 서버에 요청하여 MemberProfile 객체를 반환한다.
     *
     * @param providerName
     * @param accessToken
     * @param provider     provider의 사용자 정보 요청 주소를 담고있다.
     * @return 받아온 유저정보
     */
    private MemberProfile getMemberProfile(String providerName, String accessToken, OAuthProvider provider) {
        Map<String, Object> userAttributes = getMemberAttributes(provider, accessToken);
        return OAuthAttributes.extract(providerName, userAttributes);
    }


    /**
     * Authentication Server로 Token을 전송하여 Member 정보를 가져온다.
     */
    private Map<String, Object> getMemberAttributes(OAuthProvider provider, String accessToken) {
        return WebClient.create()
                .get()
                .uri(provider.getUserInfoUrl())
                .headers(header -> header.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(
                                new WebClientResponseException("유효하지 않은 Access token 입니다.",
                                        clientResponse.statusCode(),
                                        clientResponse.bodyToMono(String.class).toString(),
                                        clientResponse.headers().asHttpHeaders(), null, null, null)
                        ))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
