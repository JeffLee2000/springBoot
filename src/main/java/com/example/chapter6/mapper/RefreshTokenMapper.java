package com.example.chapter6.mapper;

import com.example.chapter6.model.RefreshTokenVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {

    void insertRefreshToken(RefreshTokenVO refreshTokenVO);

    boolean existMemberId(int id);

    void updateRefreshToken(RefreshTokenVO refreshTokenVO);
}
