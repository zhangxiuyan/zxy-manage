package xyz.zhangxiuyan.manage.entity.dto;

import javax.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public class RefreshTokenRequestDTO {

    @NotBlank(message = "{business.auth.refresh.token.required}")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
