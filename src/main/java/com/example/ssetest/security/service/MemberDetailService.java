package com.example.ssetest.security.service;

import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author sjChoi
 * @since 3/8/24
 */
@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member detailMember = memberRepository.findByUsername(username);
        if (detailMember != null) {
            return detailMember;
        } else {
            return null;
        }
    }
}
