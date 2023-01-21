package com.example.chapter6.ServiceImpl;

import com.example.chapter6.mapper.BoardMapper;
import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import com.example.chapter6.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardServiceImpl.class);

    private BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    /**
     * 게시물 목록
     * @return
     */
    @Override
    public List<BoardVO> selectBoardVO(SearchHelper searchHelper) {
        return boardMapper.selectBoardVO(searchHelper);
    }

    /**
     * 게시물 조회
     * @param id
     * @return
     */
    @Override
    public BoardVO selectBoardVOById(int id) {

        boardMapper.updateCount(id);
        return boardMapper.selectBoardVOById(id);
    }

    /**
     * 게시물 수정
     * @param boardVO
     */
    @Override
    public void updateBoardVO(BoardVO boardVO) {
        boardMapper.updateBoardVO(boardVO);
    }

    /**
     * 게시물 삭제
     * @param id
     */
    @Override
    public void deleteById(int id) {
        boardMapper.deleteById(id);
    }

    /**
     * 게시물 저장
     * @param boardVO
     */
    @Override
    public void insertBoardVO(BoardVO boardVO) {
        boardMapper.insertBoardVO(boardVO);
    }
}
