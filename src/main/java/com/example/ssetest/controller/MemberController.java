package com.example.ssetest.controller;

import com.example.ssetest.domain.Member;
import com.example.ssetest.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author sjChoi
 * @since 2/19/24
 */
@RestController
@RequestMapping("/apis/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<Member> memberList() {
        List<Member> memberList = memberService.memberList();
        return memberList;
    }

    @PostMapping("login")
    public Member loginMember(@RequestBody Member member, HttpServletRequest request, HttpServletResponse response) {
        Member detailMember = memberService.loginMember(member.getUsername(), request, response);
        if (detailMember != null) {
            return member;
        } else {
            return null;
        }
    }

    @PostMapping("signin")
    public String signInMember(@RequestBody Member member) {
        int result = memberService.insertMember(member);
        if (result == 1) {
            return "success";
        } else {
            return "fail";
        }
    }
}
