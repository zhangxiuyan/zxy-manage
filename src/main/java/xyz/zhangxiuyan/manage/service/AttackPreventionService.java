package xyz.zhangxiuyan.manage.service;

/**
 * 防暴力破解服务
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public interface AttackPreventionService {

    /**
     * 检查登录尝试是否允许
     *
     * @param userId    用户ID
     * @param ipAddress IP地址
     * @throws xyz.zhangxiuyan.manage.exception.AccountLockoutException 如果账户被锁定
     * @throws xyz.zhangxiuyan.manage.exception.RateLimitException      如果IP被限流
     */
    void checkLoginAttempt(String userId, String ipAddress);

    /**
     * 记录失败的登录尝试
     *
     * @param userId    用户ID
     * @param username  用户名 (用于审计日志)
     * @param ipAddress IP地址
     */
    void recordFailedAttempt(String userId, String username, String ipAddress);

    /**
     * 记录成功的登录，清除失败计数
     *
     * @param userId 用户ID
     */
    void recordSuccessfulLogin(String userId);

    /**
     * 获取用户剩余锁定时间(秒)
     *
     * @param userId 用户ID
     * @return 剩余锁定时间，0表示未锁定
     */
    long getRemainingLockoutSeconds(String userId);
}
