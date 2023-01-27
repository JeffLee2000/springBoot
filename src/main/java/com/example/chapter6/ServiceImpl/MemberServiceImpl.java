package com.example.chapter6.ServiceImpl;

import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.jwt.AuthService;
import com.example.chapter6.jwt.JwtTokenValidator;
import com.example.chapter6.jwt.RefreshTokenService;
import com.example.chapter6.mapper.MemberMapper;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.model.RefreshTokenVO;
import com.example.chapter6.payload.response.JwtAuthenticationResponse;
import com.example.chapter6.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberMapper memberMapper;
    private AuthService authService;
    private RefreshTokenService refreshTokenService;

    public MemberServiceImpl(MemberMapper memberMapper, AuthService authService, RefreshTokenService refreshTokenService) {
        this.memberMapper = memberMapper;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 아이디 중복 체크
     * @param id
     * @return
     */
    @Override
    public Boolean duplicateId(String id) {
        Boolean res = memberMapper.duplicateId(id);
        return res ? true : false;
    }

    /**
     * 이메일 중복 체크
     * @param email
     * @return
     */
    @Override
    public Boolean duplicateEmail(String email) {
        Boolean res = memberMapper.duplicateEmail(email);
        return res ? true : false;
    }

    /**
     * 회원 가입 처리
     * @param memberVO
     */
    @Override
    public void insertMember(MemberVO memberVO) throws Exception {
        memberMapper.insertMember(memberVO);
    }

    /**
     * 로그인 처리
     * @param memberVO
     * @return
     */
    @Override
    public Boolean loginProcess(MemberVO memberVO, HttpServletRequest request) {
        Optional<MemberVO>  result = memberMapper.loginProcess(memberVO);

        if (result.isPresent()) {
            // 세션 정보 생성
            HttpSession session = request.getSession();
            session.setAttribute("memberVO", result);
            session.setMaxInactiveInterval(60);
            return true;
        }

        return false;
    }

    /**
     * api 로그인 처리
     * @param memberVO
     * @return
     */
    @Override
    public JwtAuthenticationResponse loginProcess(MemberVO memberVO) {
        Optional<MemberVO>  result = memberMapper.loginProcess(memberVO);

        if (result.isPresent()) {
            /**
             * 1. fresh_token 테이블에 member 테이블의 id 값이 존재하면 토큰을 갱신한다.
             * 2. refresh_toen 테이블에 member 테이블의 id값이 존재하지 않으면 토큰을 생성 후 insert 한다.
             * 3. accessToken을 발급한다.
             * 4. refreshToken 발급한 정보와 accessToken 정보를 JSON으로 리턴.
             */
            Boolean existMemberId = refreshTokenService.existMemberId(result.get().getId());

            if (existMemberId) {
                String accessToken = authService.generateToken(result.get().getUserId());
                RefreshTokenVO refreshTokenVO = refreshTokenService.updateRefreshToken(result.get().getId());
                JwtAuthenticationResponse response = new JwtAuthenticationResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshTokenVO.getRefreshToken());
                long instant = refreshTokenVO.getExpiryDate().getEpochSecond();
                response.setExpiryDuration(instant);

                return response;
            } else {
                String accessToken = authService.generateToken(result.get().getUserId());
                RefreshTokenVO refreshTokenVO = refreshTokenService.insertRefreshToken(result.get().getId());
                JwtAuthenticationResponse response = new JwtAuthenticationResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshTokenVO.getRefreshToken());
                long instant = refreshTokenVO.getExpiryDate().getEpochSecond();
                response.setExpiryDuration(instant);

                return response;
            }

        } else {
            throw new BadRequestException("사용자 정보가 없습니다.");
        }

    }

    /**
     * 아이디 찾기
     * @param memberVO
     * @return
     */
    @Override
    public String findUserId(MemberVO memberVO) {
        return memberMapper.findUserId(memberVO);
    }

    /**
     * 비밀번호 찾기
     * @param memberVO
     * @return
     */
    @Override
    public String findPassword(MemberVO memberVO) {
        return memberMapper.findPassword(memberVO);
    }

    /**
     * 비밀번호 변경
     * @param memberVO
     */
    @Override
    public void updatePassword(MemberVO memberVO) {
        memberMapper.updatePassword(memberVO);
    }

    /**
     * 회원가입 폼 검증
     * @param errors
     * @return
     */
    @Override
    public Map<String, String> formValidation(Errors errors) {
        Map<String, String> result = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            result.put(validKeyName, error.getDefaultMessage());
        }

        return result;
    }
}
