package com.example.chapter6.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 토큰 생성
     * @param userId
     * @return
     */
    public String generateToken(String userId) {
        return jwtTokenProvider.generateToken(userId);
    }

    /**
     * 토큰으로 아이디 반환
     * @param token
     * @return
     */
    public String getUserIdFromJWT(String token) {
        return jwtTokenProvider.getUserIdFromJWT(token);
    }

    /**
     * 토큰으로 만료일 반환
     * @param token
     * @return
     */
    public Date getTokenExpiryFromJWT(String token) {
        return jwtTokenProvider.getTokenExpiryFromJWT(token);
    }
}
