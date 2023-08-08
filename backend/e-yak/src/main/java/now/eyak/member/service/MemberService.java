package now.eyak.member.service;

import now.eyak.member.domain.Member;
import now.eyak.member.dto.request.*;
import now.eyak.member.dto.response.RefreshResponseDto;
import now.eyak.member.dto.response.SignInResponseDto;

public interface MemberService {
    SignInResponseDto signIn(SignInDto signInDto) throws Exception;
    Member signUp(SignUpDto signUpDto) throws Exception;
    RefreshResponseDto issueAccessTokenByRefreshToken(ReissueDto reissueDto);
    MemberDto retrieveMember(Long memberId);
    MemberDto updateMember(MemberUpdateDto memberUpdateDto, Long memberId);
    void deleteMember(Long memberId);
    boolean isDuplicatedNickname(String nickname);
}
