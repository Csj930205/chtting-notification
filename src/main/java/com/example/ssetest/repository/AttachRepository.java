package com.example.ssetest.repository;

import com.example.ssetest.domain.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sjChoi
 * @since 3/22/24
 */
public interface AttachRepository extends JpaRepository<Attach, Long> {

    Attach findAttachByUid(Long uid);
}
