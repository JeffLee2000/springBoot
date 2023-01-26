package com.example.chapter6.jwt;

import com.example.chapter6.exception.InvalidTokenValidateException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenValidator {

    private final String jwtSecret;

    public JwtTokenValidator(@Value("${app.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public boolean validateToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        } catch (SignatureException ex) {
            log.info("서명이 안 맞다");
            throw new InvalidTokenValidateException("서명이 맞지 않습니다.");
        } catch (MalformedJwtException ex) {
            log.info("토큰 형식 안 맞다.");
            throw new InvalidTokenValidateException("토큰 형식이 맞지 않습니다.");
        } catch (UnsupportedJwtException ex) {
            log.info("지원 불가 토큰");
            throw new InvalidTokenValidateException("지원되지 않는 토큰 입니다.");
        } catch (ExpiredJwtException ex) {
            log.info("유효기간 만료");
            throw new InvalidTokenValidateException("토큰의 유효기간이 만료되었습니다.");
        } catch (IllegalArgumentException ex) {
            log.info("Claims 정보가 없다");
            throw new InvalidTokenValidateException("Claims 정보가 없습니다.");
        }

        return true;
    }
}
