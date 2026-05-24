package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户会话实体
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@TableName("user_session")
public class UserSession {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 刷新令牌哈希 (SHA-256)
     */
    private String refreshTokenHash;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 最大并发数
     */
    private Integer maxConcurrent;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;
}
