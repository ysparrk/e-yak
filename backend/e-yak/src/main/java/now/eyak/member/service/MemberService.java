package now.eyak.member.service;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.*;

public interface MemberService {
    SignInResponseDto signIn(SignInDto signInDto) throws Exception;
    Member signUp(SignUpDto signUpDto) throws Exception;
    RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto);
    MemberDto updateMember(MemberUpdateDto memberUpdateDto, Long memberId);
    void deleteMember(Long memberId);
    boolean isDuplicatedNickname(String nickname);
}
