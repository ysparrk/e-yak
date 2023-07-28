package now.eyak.member.service;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.*;
import now.eyak.member.exception.InvalidAccessTokenException;
import now.eyak.member.exception.UnsupportedProviderException;

public interface MemberService {
    SignInResponseDto signIn(SignInDto signInDto) throws InvalidAccessTokenException, UnsupportedProviderException;
    Member signUp(SignUpDto signUpDto);
    RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto);
    MemberDto updateMember(MemberUpdateDto memberUpdateDto, Long memberId);
    void deleteMember(Long memberId);
    boolean isDuplicatedNickname(String nickname);
}
