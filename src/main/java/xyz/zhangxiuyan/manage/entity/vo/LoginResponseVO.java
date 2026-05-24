package xyz.zhangxiuyan.manage.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 访问令牌过期时间(秒)
     */
    private Long expiresIn;

    /**
     * 是否需要MFA验证
     */
    private Boolean mfaRequired;

    /**
     * MFA临时令牌 (用于完成MFA验证)
     */
    private String mfaToken;

    /**
     * MFA过期时间(秒)
     */
    private Long mfaExpiresIn;
}
