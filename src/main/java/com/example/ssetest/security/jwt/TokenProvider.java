package com.example.ssetest.security.jwt;

import com.example.ssetest.domain.Member;
import com.example.ssetest.util.CookieUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final CookieUtil cookieUtil;

    @Value("${jwt.token.secretKey}")
    private String secretKey;

    @Value("${jwt.token.accessTokenValidTime}")
    private long tokenValidTime;

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token createToken(Member member) {
        Map<String, Object> header = new HashMap<>();
        header.put("type", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUsername());
        claims.put("password", member.getPassword());

        String token = Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return Token.builder()
                .key(member.getUsername())
                .value(token)
                .expiredTime(tokenValidTime)
                .build();

    }

    public void setCookieToken(HttpServletRequest request, HttpServletResponse response, String token) {
        cookieUtil.createCookie(request, response, token);
    }

    public String resolveToken(HttpServletRequest request) {
        Cookie getToken = cookieUtil.getCookie(request);
        if (getToken != null) {
            String token = getToken.getValue();
            return token;
        } else {
            return "";
        }
    }

    public HashMap<String, String> getPayloadByToken(String token) {
        try {
          String[] splitJwt = token.split("\\.");
          String payload = new String(Base64.getDecoder().decode(splitJwt[1].getBytes()));
          return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
