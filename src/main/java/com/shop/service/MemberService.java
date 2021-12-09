package com.shop.service;

import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;
import com.shop.entity.OAuth2Member;
import com.shop.repository.MemberRepository;
import com.shop.repository.OAuth2MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 회원 서비스
 *
 * @author 공통
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final OAuth2MemberRepository oAuth2MemberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);

        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());

        if(findMember != null) {
            OAuth2Member findSocialMember = oAuth2MemberRepository.findByMember(findMember);

            if(findSocialMember != null) {
                throw new IllegalStateException("해당 이메일은 소셜 로그인으로 등록된 이메일입니다, 소셜 로그인을 이용해 주세요.");
            }

            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }


    public void updatePassword(Long memberId, String password) {
        memberRepository.updatePassword(memberId, new BCryptPasswordEncoder().encode(password));
    }

    public boolean checkEmailAndName(String email, String name) {
        Member member = memberRepository.findByEmail(email);

        if(member == null || !member.getName().equals(name)) {
            throw new IllegalStateException("이메일과 이름이 일치하지 않습니다.");
        }

        return true;
    }
    /**
     *
     *
     * @param memberSearchDto 회원 검색 후 담을 dto
     * @param pageable 현재 로그인된 회원
     *
     * @return 회원레포지토리에서 getAdminMemberPage에 파라미터 담아 반환
     */
    @Operation(summary = "회원관리 조회 서비스", description = "회원관리 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원관리 조회"),
            @ApiResponse(responseCode = "400", description = "회원관리 조회 실패")})
    @Transactional(readOnly = true)
    public Page<Member> getAdminMemberPage(@Parameter(description = "회원 검색 dto")MemberSearchDto memberSearchDto, @Parameter(description = "페이지 기능")Pageable pageable){
        return memberRepository.getAdminMemberPage(memberSearchDto, pageable);
    }

}
