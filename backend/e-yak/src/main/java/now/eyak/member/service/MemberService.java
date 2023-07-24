package now.eyak.member.service;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.*;
import now.eyak.member.exception.InvalidAccessTokenException;
import now.eyak.member.exception.UnsupportedProviderException;

public interface MemberService {
    SignInResponseDto signIn(SignInDto signInDto) throws InvalidAccessTokenException, UnsupportedProviderException;
    Member signUp(SignUpDto signUpDto);

    public RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto);
}
