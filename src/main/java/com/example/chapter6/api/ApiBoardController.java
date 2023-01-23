package com.example.chapter6.api;

import com.example.chapter6.Util.ExceptionMessage;
import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.exception.InsertFailException;
import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import com.example.chapter6.payload.response.ApiResponse;
import com.example.chapter6.service.BoardService;
import com.example.chapter6.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
public class ApiBoardController {

    private static final Logger logger = LoggerFactory.getLogger(ApiBoardController.class);

    private BoardService boardService;
    private MemberService memberService;

    public ApiBoardController(BoardService boardService, MemberService memberService) {
        this.boardService = boardService;
        this.memberService = memberService;
    }

    @GetMapping("/test/{id}")
    public ApiResponse test(@PathVariable int id) {
        if (id == 2) throw new BadRequestException("잘못된 요청입니다.");
        return new ApiResponse(true, "정상출력");
    }

    /**
     * 게시물 목록
     * @param searchHelper
     */
    @GetMapping("/list")
    public ResponseEntity boardList(
            @RequestBody SearchHelper searchHelper
            ) throws Exception {

        HashMap<String, Object> result = boardService.selectBoardVO(searchHelper);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 게시물 조회
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    public  ResponseEntity boardView(
            @PathVariable int id
    ) throws Exception {
        Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);
        if (boardVO.isPresent()) {
            return new ResponseEntity(boardVO, HttpStatus.OK);
        } else {
            throw new BadRequestException(ExceptionMessage.ARTICLE_NOT_FOUND);
        }
    }

    /**
     * 게시물 저장
     * @param boardVO
     * @return
     */
    @PostMapping("/save")
    public ApiResponse boardSave(
            @RequestBody @Valid BoardVO boardVO, Errors errors
    ) throws Exception {

        HashMap<String, Object> errorMap = new HashMap<>();

        if (errors.hasErrors()) {
            Map<String, String> validate = boardService.formValidation(errors);

            for (String key : validate.keySet()) {
                errorMap.put(key, validate.get(key));
            }

            return new ApiResponse(false, ExceptionMessage.SAVE_FAIL, errorMap);
        }

        boardVO.setRegId("api");
        try {
            boardService.insertBoardVO(boardVO);
        } catch (Exception e) {
            throw new InsertFailException(ExceptionMessage.SAVE_FAIL);
        }
        return new ApiResponse(true, ExceptionMessage.SAVE_SUCCESS);
    }

    /**
     * 게시물 수정
     * @param boardVO
     * @return
     * @throws Exception
     */
    @PutMapping("/update")
    public ApiResponse boardUpdate(
            @RequestBody BoardVO boardVO
    ) throws Exception {
        boardVO.setRegId("api");
        try {
            boardService.updateBoardVO(boardVO);
        } catch (Exception e) {
            throw new InsertFailException(ExceptionMessage.SAVE_FAIL);
        }
        return new ApiResponse(true, ExceptionMessage.SAVE_SUCCESS);
    }

    /**
     * 게시물 삭제
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse boardDelete(
            @PathVariable int id
    ) throws Exception {

        Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);

        if (boardVO.isPresent()) {
            try {
                boardService.deleteById(id);
            } catch (Exception e) {
                throw new InsertFailException(ExceptionMessage.DELETE_FAIL);
            }
        } else {
            throw new InsertFailException(ExceptionMessage.NOT_FOUND_ARTICLE);
        }
        return new ApiResponse(true, ExceptionMessage.DELETE_SUCCESS);
    }

}
