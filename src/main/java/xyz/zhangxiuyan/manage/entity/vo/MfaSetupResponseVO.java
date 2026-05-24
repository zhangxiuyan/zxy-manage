package xyz.zhangxiuyan.manage.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MFA设置响应VO
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResponseVO {

    /**
     * 密钥 (Base32编码)
     */
    private String secretKey;

    /**
     * QR码Base64图片
     */
    private String qrCodeBase64;

    /**
     * otpauth URL
     */
    private String otpauthUrl;

    /**
     * 备份码列表
     */
    private List<String> backupCodes;
}
