package com.example.chapter6.api;

import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import com.example.chapter6.payload.response.ApiResponse;
import com.example.chapter6.service.BoardService;
import com.example.chapter6.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
        BoardVO boardVO = boardService.selectBoardVOById(id);
        return new ResponseEntity(boardVO, HttpStatus.OK);
    }

    /**
     * 게시물 저장
     * @param boardVO
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity boardSave(
            @RequestBody BoardVO boardVO
    ) throws Exception {
        boardVO.setRegId("api");
        boardService.insertBoardVO(boardVO);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity boardUpdate(
            @RequestBody BoardVO boardVO
    ) throws Exception {
        boardVO.setRegId("api");
        boardService.updateBoardVO(boardVO);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity boardDelete(
            @PathVariable int id
    ) throws Exception {
        boardService.deleteById(id);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
