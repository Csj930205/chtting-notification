package com.example.ssetest.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Configuration
@RequiredArgsConstructor
public class CookieUtil {

    private final String PATH = "/";

    public Cookie getCookie(HttpServletRequest request) {
        Cookie[] getCookie = request.getCookies();
        Cookie cookie = null;

        if (getCookie != null) {
            for (Cookie cookies : getCookie) {
                if (cookies.getName().equals("Authorization")) {
                    cookie = cookies;
                }
            }
        }
        return cookie;
    }

    public void createCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        String domain = request.getServerName();
        Cookie tokenCookie = new Cookie("Authorization", token);
        tokenCookie.setMaxAge(60 * 60 * 24 * 30);
        tokenCookie.setPath(PATH);
        tokenCookie.setDomain(domain);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addCookie(tokenCookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie getCookie = getCookie(request);
        if (getCookie != null) {
            Cookie cookieDelete = new Cookie(getCookie.getName(), null);
            cookieDelete.setMaxAge(0);
            cookieDelete.setPath(PATH);
            response.addCookie(cookieDelete);
        }
    }


}
