package com.example.ssetest.repository;

import com.example.ssetest.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

    List<Member> findAll();
}
