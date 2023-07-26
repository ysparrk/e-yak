package now.eyak.util.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.member.exception.InvalidRefreshTokenException;
import now.eyak.member.exception.NoSuchMemberException;
import now.eyak.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    private final Key KEY = Keys.hmacShaKeyFor(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    private static final String HEADER_KEY = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private final MemberRepository memberRepository;

    public JwtTokenProvider(
            @Value("${jwt.properties.access-token-expiration-time-in-milliseconds}") long ACCESS_TOKEN_EXPIRATION_TIME,
            @Value("${jwt.properties.refresh-token-expiration-time-in-milliseconds}") long REFRESH_TOKEN_EXPIRATION_TIME,
            MemberRepository memberRepository) {
        this.ACCESS_TOKEN_EXPIRATION_TIME = ACCESS_TOKEN_EXPIRATION_TIME;
        this.REFRESH_TOKEN_EXPIRATION_TIME = REFRESH_TOKEN_EXPIRATION_TIME;
        this.memberRepository = memberRepository;
    }

    public String bulidAccessToken(Member member) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .addClaims(Map.of("id", member.getId()))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String buildRefreshToken(Member member) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .addClaims(Map.of("id", member.getId()))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims parseToken(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Claims claims = getJwtParser()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    public Claims parseRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Claims claims = getJwtParser()
                .parseClaimsJws(refreshToken)
                .getBody();

        if (!claims.getSubject().equals(REFRESH_TOKEN_SUBJECT)) {
            throw new InvalidRefreshTokenException("유효한 Refresh Token이 아닙니다.");
        }

        return claims;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        Long id = claims.get("id", Long.class);

        // AuthenticationManager 거치지 않고 Authentication 진행
        Member member = memberRepository.findById(id).orElseThrow(() -> new NoSuchMemberException("토큰에 맞는 사용자정보가 없습니다."));

        // 현재 권한은 하나만 부여가능
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(member.getRole().getKey())
        );

//        return UsernamePasswordAuthenticationToken.aumthenticated(member, token, authorities);
        return new UsernamePasswordAuthenticationToken(member.getId(), token, authorities);
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    private JwtParser getJwtParser() {
        return Jwts
                .parserBuilder()
                .setSigningKey(KEY)
                .build();
    }
}
