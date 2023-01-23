package com.example.chapter6.api;

import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
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

    /**
     * 게시물 목록
     * @param searchHelper
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity boardList(
            @RequestBody SearchHelper searchHelper
            ) {
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
    ) {
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
    ) {
        boardVO.setRegId("api");
        boardService.insertBoardVO(boardVO);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity boardUpdate(
            @RequestBody BoardVO boardVO
    ) {
        boardVO.setRegId("api");
        boardService.updateBoardVO(boardVO);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity boardDelete(
            @PathVariable int id
    ) {
        boardService.deleteById(id);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
