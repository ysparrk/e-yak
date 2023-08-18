package now.eyak.util.jwt;

import com.nimbusds.jose.JOSEException;
import now.eyak.member.domain.Member;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

public interface JwtTokenProvider {
    String bulidAccessToken(Member member) throws JOSEException, ParseException;

    String buildRefreshToken(Member member) throws JOSEException;

    Object parseTokenWithoutValidation(String token) throws ParseException, JOSEException;

    Object parseAccessToken(String token) throws ParseException, JOSEException;

    Object parseRefreshToken(String refreshToken) throws ParseException, JOSEException;

    Authentication getAuthentication(String token) throws ParseException, JOSEException;

    void validateJwtWithJwk(String token, String jwkStr) throws JOSEException, ParseException;

}
