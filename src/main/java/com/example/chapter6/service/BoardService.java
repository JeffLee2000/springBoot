package com.example.chapter6.service;

import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;

import java.util.HashMap;
import java.util.List;

public interface BoardService {

    HashMap<String, Object> selectBoardVO(SearchHelper searchHelper);

    BoardVO selectBoardVOById(int id);

    void updateBoardVO(BoardVO boardVO);

    void deleteById(int id);

    void insertBoardVO(BoardVO boardVO);

}
