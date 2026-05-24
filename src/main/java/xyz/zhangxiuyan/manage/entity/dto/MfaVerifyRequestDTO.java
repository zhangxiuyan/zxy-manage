package xyz.zhangxiuyan.manage.entity.dto;

import javax.validation.constraints.NotBlank;

/**
 * MFA验证请求DTO
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public class MfaVerifyRequestDTO {

    @NotBlank(message = "{business.auth.mfa.token.required}")
    private String mfaToken;

    /**
     * MFA临时令牌 (从登录响应中获取)
     */
    private String mfaTempToken;

    public String getMfaToken() {
        return mfaToken;
    }

    public void setMfaToken(String mfaToken) {
        this.mfaToken = mfaToken;
    }

    public String getMfaTempToken() {
        return mfaTempToken;
    }

    public void setMfaTempToken(String mfaTempToken) {
        this.mfaTempToken = mfaTempToken;
    }
}
