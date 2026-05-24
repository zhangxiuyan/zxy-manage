package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.entity.LoginLog;
import xyz.zhangxiuyan.manage.mapper.LoginLogMapper;
import xyz.zhangxiuyan.manage.service.LoginLogService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志服务实现
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public void logSuccess(Long userId, String username, String loginType, String ipAddress, String deviceInfo, String userAgent) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginType(loginType);
        log.setStatus("SUCCESS");
        log.setIpAddress(ipAddress);
        log.setDeviceInfo(deviceInfo);
        log.setUserAgent(userAgent);
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public void logFailed(Long userId, String username, String loginType, String ipAddress, String deviceInfo, String userAgent, String failReason) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginType(loginType);
        log.setStatus("FAILED");
        log.setIpAddress(ipAddress);
        log.setDeviceInfo(deviceInfo);
        log.setUserAgent(userAgent);
        log.setFailReason(failReason);
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public void logLockout(Long userId, String username, String ipAddress, long lockoutDurationSeconds) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginType("PASSWORD");
        log.setStatus("LOCKED");
        log.setIpAddress(ipAddress);
        log.setFailReason("Account locked due to " + lockoutDurationSeconds + " seconds of failed attempts");
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public List<LoginLog> getLogsByUserId(Long userId, int limit) {
        return list(new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getUserId, userId)
                .orderByDesc(LoginLog::getCreatedAt)
                .last("LIMIT " + limit));
    }

    @Override
    public List<LoginLog> getLogsByIpAddress(String ipAddress, int limit) {
        return list(new LambdaQueryWrapper<LoginLog>()
                .eq(LoginLog::getIpAddress, ipAddress)
                .orderByDesc(LoginLog::getCreatedAt)
                .last("LIMIT " + limit));
    }
}
