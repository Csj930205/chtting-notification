package com.example.ssetest.security.filter;

import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.MemberRepository;
import com.example.ssetest.security.jwt.TokenProvider;
import com.example.ssetest.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String token = tokenProvider.resolveToken(request);

        if (path.equals("/apis/member/login") || token == null) {
            System.out.println("=====================");
            System.out.println("로그인중");
            System.out.println("로그인 path: " + path);
            System.out.println("=====================");
            filterChain.doFilter(request, response);
        } else {
            setAuthentication(token);
            System.out.println("=====================");
            System.out.println("path: " + path);
            System.out.println("필터통과");
            System.out.println("=====================");
            filterChain.doFilter(request, response);
        }
    }

    private void setAuthentication(String token) {
        Member member = getMemberUid(token);
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, "", member.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Member getMemberUid(String token) {
        HashMap<String, String> payload = tokenProvider.getPayloadByToken(token);
        Member detailMember = memberRepository.findByUsername(payload.get("username"));
        if (detailMember != null) {
            return detailMember;
        } else {
            return null;
        }
    }

}
