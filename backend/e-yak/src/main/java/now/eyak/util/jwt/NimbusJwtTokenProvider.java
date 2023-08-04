package now.eyak.util.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class NimbusJwtTokenProvider implements JwtTokenProvider {
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    private final Key KEY = Keys.hmacShaKeyFor(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    private static final String HEADER_KEY = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private final MemberRepository memberRepository;
    private final byte[] sharedSecret;

    public NimbusJwtTokenProvider(
            @Value("${jwt.properties.access-token-expiration-time-in-milliseconds}") long ACCESS_TOKEN_EXPIRATION_TIME,
            @Value("${jwt.properties.refresh-token-expiration-time-in-milliseconds}") long REFRESH_TOKEN_EXPIRATION_TIME,
            MemberRepository memberRepository) {
        this.ACCESS_TOKEN_EXPIRATION_TIME = ACCESS_TOKEN_EXPIRATION_TIME;
        this.REFRESH_TOKEN_EXPIRATION_TIME = REFRESH_TOKEN_EXPIRATION_TIME;
        this.memberRepository = memberRepository;

        // Generate random 256-bit (32-byte) shared secret
        SecureRandom random = new SecureRandom();
        sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);
    }

    @Override
    public String bulidAccessToken(Member member) throws JOSEException, ParseException {
        JWSSigner signer = new MACSigner(sharedSecret);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(ACCESS_TOKEN_SUBJECT)
                .expirationTime(new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .claim("id", member.getId())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        String s = signedJWT.serialize();

        return s;
    }

    @Override
    public String buildRefreshToken(Member member) throws JOSEException {
        JWSSigner signer = new MACSigner(sharedSecret);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(REFRESH_TOKEN_SUBJECT)
                .expirationTime(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .claim("id", member.getId())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        String s = signedJWT.serialize();

        return s;
    }

    @Override
    public Object parseTokenWithoutValidation(String token) throws ParseException, JOSEException {
        return SignedJWT.parse(token);
    }

    @Override
    public Object parseAccessToken(String token) {
        SignedJWT signedJWT = null;
        try {
             signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(sharedSecret);

            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Access 토큰이 유효하지 않습니다.");
            }

            if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("Access 토큰이 만료되었습니다.");
            }

        } catch (ParseException|JOSEException e) {
            throw new IllegalArgumentException("Refresh 토큰이 유효하지 않습니다.");
        }

        return signedJWT;
    }

    @Override
    public Object parseRefreshToken(String refreshToken) {
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier verifier = new MACVerifier(sharedSecret);

            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Refresh 토큰이 유효하지 않습니다.");
            }

            if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("Refresh 토큰이 만료되었습니다.");
            }
        } catch (ParseException|JOSEException e) {
            throw new IllegalArgumentException("Refresh 토큰이 유효하지 않습니다. " + e.getMessage());
        }


        return signedJWT;
    }

    @Override
    public void validateJwtWithJwk(String token, String jwkStr) throws JOSEException, ParseException {
        try {
            log.debug("JWK를 이용하여 JWT 검증 시작... token: {}, jwkStr: {}", token, jwkStr);

            SignedJWT signedJWT = SignedJWT.parse(token);
            log.debug("검증 대상 토큰의 Calims signedJWT: {}", signedJWT.getJWTClaimsSet().toString());
            JWKSelector jwkSelector = new JWKSelector(JWKMatcher.forJWSHeader(signedJWT.getHeader()));

            log.debug("JWK String을 파싱...");
            JWKSet jwkSet = JWKSet.parse(jwkStr);
            log.debug("JWK String 파싱 결과 jwkSet: {}", jwkSet.getKeys());
            JWK jwk = jwkSelector.select(jwkSet).get(0);

            PublicKey publicKey = jwk.toRSAKey().toPublicKey();

            RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("ID Token이 유효하지 않습니다.");
            }

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            if (!(jwtClaimsSet.getStringClaim("iss").equals("https://accounts.google.com") || jwtClaimsSet.getStringClaim("aud").equals("309095971957-gv61u52m9r7v5nbouqtutmvapq99ogug.apps.googleusercontent.com"))) {
                throw new IllegalArgumentException(("idToken의 iss 혹은 aud가 일치하지 않습니다."));
            }

            if (jwtClaimsSet.getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("idToken이 만료되었습니다.");
            }
        } catch (JOSEException|ParseException e) {
            throw new IllegalArgumentException("id Token이 유효하지 않습니다. " + e.getMessage());
        }

    }

    @Override
    public Authentication getAuthentication(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = (SignedJWT) parseAccessToken(token);
        Long id = signedJWT.getJWTClaimsSet().getLongClaim("id");

        // AuthenticationManager 거치지 않고 Authentication 진행
        Member member = memberRepository.findById(id).orElseThrow(() -> new NoSuchMemberException("토큰에 맞는 사용자정보가 없습니다."));

        // 현재 권한은 하나만 부여가능
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(member.getRole().getKey())
        );

        return new UsernamePasswordAuthenticationToken(member.getId(), token, authorities);
    }

}
