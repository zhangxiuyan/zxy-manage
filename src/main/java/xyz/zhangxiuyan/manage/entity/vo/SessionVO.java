package xyz.zhangxiuyan.manage.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话VO
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionVO {

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 是否当前会话
     */
    private Boolean isCurrent;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessedAt;
}
