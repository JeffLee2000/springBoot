package com.example.chapter6.api;

import com.example.chapter6.Util.ExceptionMessage;
import com.example.chapter6.Util.Util;
import com.example.chapter6.event.OnLogoutSuccessEvent;
import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.exception.InsertFailException;
import com.example.chapter6.exception.ResourceAlreadyUseException;
import com.example.chapter6.exception.UserNotFoundException;
import com.example.chapter6.jwt.AuthService;
import com.example.chapter6.mapper.BoardMapper;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.payload.request.LoginRequest;
import com.example.chapter6.payload.response.ApiResponse;
import com.example.chapter6.payload.response.JwtAuthenticationResponse;
import com.example.chapter6.service.MemberService;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class ApiMemberController {

    private static final Logger logger = LoggerFactory.getLogger(ApiMemberController.class);

    private MemberService memberService;
    private AuthService authService;
    private ApplicationEventPublisher applicationEventPublisher;

    public ApiMemberController(MemberService memberService, AuthService authService, ApplicationEventPublisher applicationEventPublisher) {
        this.memberService = memberService;
        this.authService = authService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");
        accessToken = accessToken.replace("Bearer", "");
        // ?????? ??????...
        String res = authService.getUserIdFromJWT(accessToken);
        //test id
        OnLogoutSuccessEvent event = new OnLogoutSuccessEvent(res, accessToken);
        applicationEventPublisher.publishEvent(event);

        return new ApiResponse(true, "??????");
    }


    /**
     * ????????? ??????
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity loginProcess(@RequestBody @Valid LoginRequest loginRequest) {
        MemberVO memberVO = new MemberVO();
        memberVO.setUserId(loginRequest.getUserId());
        memberVO.setPassword(loginRequest.getPassword());

        JwtAuthenticationResponse result = memberService.loginProcess(memberVO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/apiTest")
    public ApiResponse loginProcess(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        logger.info("???????????? ????????? ID - {}", authService.getUserIdFromJWT(token));
        logger.info("???????????? ????????? ????????? - {}", authService.getTokenExpiryFromJWT(token));
        return new ApiResponse(true, "??????");
    }

    /**
     * ????????? ?????? ?????? ??????
     * @param userId
     * @return
     */
    @GetMapping("/exist/id/{userId}")
    public ApiResponse existId(@PathVariable String userId) throws Exception {
        if (userId.equals("")) throw new BadRequestException(ExceptionMessage.EMPTY_USER_ID);

        Boolean result = memberService.duplicateId(userId);

        // true??? ???????????? ???????????? ???????????? ??????.
        if (result) return new ApiResponse(false, "???????????? ??????????????????.");

        return new ApiResponse(true, ExceptionMessage.USE_ID_AVAILABLE);
    }

    /**
     * ????????? ?????? ?????? ??????
     * @param email
     * @return
     * @throws Exception
     */
    @GetMapping("/exist/email/{email}")
    public ApiResponse existEmail(@PathVariable String email) throws Exception {
        if (email.equals("")) throw new BadRequestException(ExceptionMessage.EMPTY_USER_ID);

        Boolean result = memberService.duplicateEmail(email);

        // true??? ???????????? ???????????? ???????????? ??????.
        if (result) return new ApiResponse(false, "???????????? ??????????????????.");

        return new ApiResponse(true, ExceptionMessage.USE_EMAIL_AVAILABLE);
    }

    /**
     * ????????? ??????
     * @param name
     * @param email
     * @return
     */
    @GetMapping("/find/id/{name}/{email}")
    public ApiResponse findId(
            @PathVariable String name,
            @PathVariable String email
    ) {
        if (!name.equals("") && !email.equals("")) {
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);
            try {
                String id = memberService.findUserId(memberVO);
                if (id == null) {
                    throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
                }
                int idLength = id.length();
                id = id.substring(0, idLength - 2);

                id += "**";
                return new ApiResponse(true, name + "?????? ???????????? ID??? " + id + "?????????.");
            } catch (Exception e) {
                throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
            }
        }
        throw new BadRequestException(ExceptionMessage.EMPTY_INFO);
    }

    @GetMapping("/find/password/{name}/{email}/{userId}")
    public ApiResponse findPw (
            @PathVariable String name,
            @PathVariable String email,
            @PathVariable String userId
    ) {
        if (!name.equals("") && !email.equals("")) {
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);
            memberVO.setUserId(userId);
            try {
                String id = memberService.findPassword(memberVO);
                if (id == null) {
                    throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
                }
                String pw = Util.generateRandomString(5);
                memberVO.setPassword(pw);
                memberService.updatePassword(memberVO);
                return new ApiResponse(true, "??????????????? ?????????????????????.");
            } catch (Exception e) {
                throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
            }
        }
        throw new BadRequestException(ExceptionMessage.EMPTY_INFO);
    }

    /**
     * ?????? ?????? ??????
     * @param memberVO
     * @param errors
     * @return
     */
    @PostMapping("/join")
    public ApiResponse memberJoin(@RequestBody @Valid MemberVO memberVO, Errors errors) {

        HashMap<String, Object> errorMap = new HashMap<>();

        if (errors.hasErrors()) {
            Map<String, String> validate = memberService.formValidation(errors);

            for (String key : validate.keySet()) {
                errorMap.put(key, validate.get(key));
            }

            return new ApiResponse(false, ExceptionMessage.SAVE_FAIL, errorMap);
        }

        boolean idCheck = false;
        boolean emailCheck = false;
        try {
            idCheck = memberService.duplicateId(memberVO.getUserId());
            emailCheck = memberService.duplicateEmail(memberVO.getEmail());
        } catch (Exception e) {
            throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
        }
        if (!idCheck && !emailCheck) {
            try {
                memberService.insertMember((memberVO));
                return new ApiResponse(true, ExceptionMessage.JOIN_COMPLETE);
            } catch (Exception e) {
                throw new InsertFailException(ExceptionMessage.SAVE_FAIL);
            }
        }
        throw new InsertFailException(ExceptionMessage.SAVE_FAIL);

    }

}
