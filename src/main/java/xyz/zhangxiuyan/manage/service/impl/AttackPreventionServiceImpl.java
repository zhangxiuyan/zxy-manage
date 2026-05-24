package xyz.zhangxiuyan.manage.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.exception.AccountLockoutException;
import xyz.zhangxiuyan.manage.exception.RateLimitException;
import xyz.zhangxiuyan.manage.service.AttackPreventionService;
import xyz.zhangxiuyan.manage.service.LoginLogService;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 防暴力破解服务实现
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Service
public class AttackPreventionServiceImpl implements AttackPreventionService {

    private static final String LOGIN_ATTEMPTS_USER_PREFIX = "login_attempts:";
    private static final String LOGIN_ATTEMPTS_IP_PREFIX = "login_attempts:ip:";
    private static final String LOGIN_LOCKOUT_PREFIX = "login_lockout:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LoginLogService loginLogService;

    /**
     * 最大失败尝试次数
     */
    @Value("${security.login-attempt.max-attempts:5}")
    private int maxAttempts;

    /**
     * 账户锁定时长(分钟)
     */
    @Value("${security.login-attempt.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    /**
     * 尝试计数窗口(分钟)
     */
    @Value("${security.login-attempt.window-minutes:15}")
    private int windowMinutes;

    /**
     * IP限流阈值
     */
    @Value("${security.login-attempt.ip-rate-limit:20}")
    private int ipRateLimit;

    /**
     * IP限流窗口(分钟)
     */
    @Value("${security.login-attempt.ip-rate-limit-window-minutes:1}")
    private int ipRateLimitWindowMinutes;

    @Override
    public void checkLoginAttempt(String userId, String ipAddress) {
        // Check if account is locked
        String lockoutKey = LOGIN_LOCKOUT_PREFIX + userId;
        String lockoutEnd = stringRedisTemplate.opsForValue().get(lockoutKey);
        if (lockoutEnd != null) {
            long lockoutEndTime = Long.parseLong(lockoutEnd);
            if (System.currentTimeMillis() < lockoutEndTime) {
                long remainingSeconds = (lockoutEndTime - System.currentTimeMillis()) / 1000;
                throw new AccountLockoutException(
                        "Account is temporarily locked due to too many failed login attempts",
                        remainingSeconds
                );
            } else {
                // Lockout expired, clean up
                stringRedisTemplate.delete(lockoutKey);
                stringRedisTemplate.delete(LOGIN_ATTEMPTS_USER_PREFIX + userId);
            }
        }

        // Check IP rate limit
        String ipAttemptKey = LOGIN_ATTEMPTS_IP_PREFIX + ipAddress;
        String ipAttempts = stringRedisTemplate.opsForValue().get(ipAttemptKey);
        if (ipAttempts != null && Integer.parseInt(ipAttempts) > ipRateLimit) {
            throw new RateLimitException("Too many login attempts from this IP address. Please try again later.");
        }
    }

    @Override
    public void recordFailedAttempt(String userId, String username, String ipAddress) {
        String attemptKeyUser = LOGIN_ATTEMPTS_USER_PREFIX + userId;
        String attemptKeyIp = LOGIN_ATTEMPTS_IP_PREFIX + ipAddress;
        String lockoutKey = LOGIN_LOCKOUT_PREFIX + userId;

        // Increment user attempts
        Long attempts = stringRedisTemplate.opsForValue().increment(attemptKeyUser);
        stringRedisTemplate.expire(attemptKeyUser, windowMinutes, TimeUnit.MINUTES);

        // Increment IP attempts
        stringRedisTemplate.opsForValue().increment(attemptKeyIp);
        stringRedisTemplate.expire(attemptKeyIp, ipRateLimitWindowMinutes, TimeUnit.MINUTES);

        // Lock account after max attempts reached
        if (attempts != null && attempts >= maxAttempts) {
            long lockoutDurationMs = calculateLockoutDuration(attempts);
            long lockoutEnd = System.currentTimeMillis() + lockoutDurationMs;
            stringRedisTemplate.opsForValue().set(
                    lockoutKey,
                    String.valueOf(lockoutEnd),
                    lockoutDurationMs,
                    TimeUnit.MILLISECONDS
            );

            // Log account lockout
            long lockoutDurationSeconds = lockoutDurationMs / 1000;
            loginLogService.logLockout(Long.parseLong(userId), username, ipAddress, lockoutDurationSeconds);
        }
    }

    @Override
    public void recordSuccessfulLogin(String userId) {
        // Reset failed attempts
        stringRedisTemplate.delete(LOGIN_ATTEMPTS_USER_PREFIX + userId);
        stringRedisTemplate.delete(LOGIN_LOCKOUT_PREFIX + userId);
    }

    @Override
    public long getRemainingLockoutSeconds(String userId) {
        String lockoutKey = LOGIN_LOCKOUT_PREFIX + userId;
        String lockoutEnd = stringRedisTemplate.opsForValue().get(lockoutKey);
        if (lockoutEnd == null) {
            return 0;
        }
        long lockoutEndTime = Long.parseLong(lockoutEnd);
        if (System.currentTimeMillis() >= lockoutEndTime) {
            // Lockout expired
            stringRedisTemplate.delete(lockoutKey);
            stringRedisTemplate.delete(LOGIN_ATTEMPTS_USER_PREFIX + userId);
            return 0;
        }
        return (lockoutEndTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 计算锁定时长（指数退避）
     * 5次失败：15分钟
     * 10次失败：30分钟
     * 15次失败：60分钟
     * ...
     */
    private long calculateLockoutDuration(long attempts) {
        int lockoutMultiplier = (int) ((attempts - maxAttempts) / maxAttempts);
        long baseDurationMs = lockoutDurationMinutes * 60 * 1000L;
        return baseDurationMs * (long) Math.pow(2, Math.max(0, lockoutMultiplier));
    }
}
