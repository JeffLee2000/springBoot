package com.example.chapter6.service;

import com.example.chapter6.model.MemberVO;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

public interface MemberService {

    Boolean duplicateId(String id);

    Boolean duplicateEmail(String email);

    void insertMember(MemberVO memberVO) throws Exception;

    Boolean loginProcess(MemberVO memberVO, HttpServletRequest request);

    Optional<MemberVO> loginProcess(MemberVO memberVO);

    String findUserId(MemberVO memberVO);

    String findPassword(MemberVO memberVO);

    void updatePassword(MemberVO memberVO);

    Map<String, String> formValidation(Errors errors);

}
