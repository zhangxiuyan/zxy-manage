package xyz.zhangxiuyan.manage.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MfaVerifyRequestDTO {

    @NotBlank(message = "{business.auth.mfa.token.required}")
    private String mfaToken;

    /** MFA临时令牌 (从登录响应中获取) */
    private String mfaTempToken;
}
