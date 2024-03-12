package com.example.ssetest.service;

import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.MemberRepository;
import com.example.ssetest.security.jwt.Token;
import com.example.ssetest.security.jwt.TokenProvider;
import com.example.ssetest.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;


    @Transactional(readOnly = true)
    public List<Member> memberList() {
        List<Member> memberList = memberRepository.findAll();
        return memberList;
    }

    @Transactional(readOnly = true)
    public Member detailMember(String username) {
        Member detailMember = memberRepository.findByUsername(username);

        if (detailMember != null) {
            return detailMember;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Member loginMember(String username, HttpServletRequest request, HttpServletResponse response) {
        Member detailMember = memberRepository.findByUsername(username);
        Token token = tokenProvider.createToken(detailMember);
        tokenProvider.setCookieToken(request, response, token.getValue());
        if (detailMember != null) {
            return detailMember;
        } else {
            return null;
        }
    }

    @Transactional
    public int insertMember(Member member) {
        Member detailMember = detailMember(member.getUsername());
        if (detailMember == null) {
            memberRepository.save(member);
            return 1;
        } else {
            return 0;
        }
    }
}
