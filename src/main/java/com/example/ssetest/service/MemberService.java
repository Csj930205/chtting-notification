package com.example.ssetest.service;

import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
