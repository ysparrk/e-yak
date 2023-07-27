package now.eyak.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import now.eyak.member.domain.Member;
import now.eyak.util.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = null;

        try {
            log.debug("authenticationHeaderValue: {}", request.getHeader(AUTHORIZATION_HEADER_NAME));
            String token = resolveToken(request.getHeader(AUTHORIZATION_HEADER_NAME));
            authentication = jwtTokenProvider.getAuthentication(token);

            log.debug("authentication.isAuthenticated(): {}", authentication.isAuthenticated());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다.", ((Member) authentication.getPrincipal()).getNickname());
        } catch (Exception e) {
            log.info("JWT 토큰이 없거나 유효하지 않습니다, {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(String authorizationHeaderValue) {
        if (!authorizationHeaderValue.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            throw new IllegalArgumentException("유효하지 않은 Authorization header value 입니다.");
        }

        return authorizationHeaderValue.substring(AUTHORIZATION_HEADER_PREFIX.length());
    }
}
