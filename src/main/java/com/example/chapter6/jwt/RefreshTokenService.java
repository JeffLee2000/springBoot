package com.example.chapter6.jwt;

import com.example.chapter6.mapper.RefreshTokenMapper;
import com.example.chapter6.model.RefreshTokenVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.plaf.PanelUI;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private RefreshTokenMapper refreshTokenMapper;

    @Value("${app.jwt.token.refresh.duration}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenMapper = refreshTokenMapper;
    }

    public RefreshTokenVO insertRefreshToken(int id) {
        RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
        refreshTokenVO.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenVO.setMemberId(id);
        refreshTokenVO.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenMapper.insertRefreshToken(refreshTokenVO);
        return refreshTokenVO;
    }

    /**
     * member_id 존재 유무
     * @param id
     * @return
     */
    public boolean existMemberId(int id) {
        return refreshTokenMapper.existMemberId(id);
    }

    /**
     * refresh_token 갱신
     * @param id
     */
    public RefreshTokenVO updateRefreshToken(int id) {
        RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
        refreshTokenVO.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenVO.setMemberId(id);
        refreshTokenVO.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenMapper.updateRefreshToken(refreshTokenVO);
        return refreshTokenVO;
    }

}
