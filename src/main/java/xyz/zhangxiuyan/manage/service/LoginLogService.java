package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.LoginLog;

import java.util.List;

/**
 * 登录日志服务接口
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public interface LoginLogService {

    /**
     * 记录成功登录
     */
    void logSuccess(Long userId, String username, String loginType, String ipAddress, String deviceInfo, String userAgent);

    /**
     * 记录失败登录
     */
    void logFailed(Long userId, String username, String loginType, String ipAddress, String deviceInfo, String userAgent, String failReason);

    /**
     * 记录账户锁定
     */
    void logLockout(Long userId, String username, String ipAddress, long lockoutDurationSeconds);

    /**
     * 查询用户的登录日志
     */
    List<LoginLog> getLogsByUserId(Long userId, int limit);

    /**
     * 查询IP的登录日志
     */
    List<LoginLog> getLogsByIpAddress(String ipAddress, int limit);
}
