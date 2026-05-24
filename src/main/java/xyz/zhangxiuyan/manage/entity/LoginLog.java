package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@TableName("login_log")
public class LoginLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名 (冗余存储，便于查询)
     */
    private String username;

    /**
     * 登录类型: PASSWORD, MFA, REFRESH
     */
    private String loginType;

    /**
     * 状态: SUCCESS, FAILED, LOCKED
     */
    private String status;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
