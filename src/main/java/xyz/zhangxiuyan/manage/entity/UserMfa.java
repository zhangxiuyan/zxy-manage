package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户MFA配置实体
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@TableName("user_mfa")
public class UserMfa {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * MFA类型: TOTP, SMS, EMAIL
     */
    private String mfaType;

    /**
     * 加密存储的密钥
     */
    private String secretKey;

    /**
     * 加密存储的备份码 (JSON array)
     */
    private String backupCodes;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
