package com.example.ssetest.util;

import com.example.ssetest.domain.Member;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Component
public final class SecurityUtil {

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return !AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

    public static Member getCurrentMember() {
        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Member) {
                return (Member) authentication.getPrincipal();
            }
        }
        return null;
    }
}
