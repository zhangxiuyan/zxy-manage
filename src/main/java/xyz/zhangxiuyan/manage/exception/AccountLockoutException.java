package xyz.zhangxiuyan.manage.exception;

/**
 * 账户锁定异常
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public class AccountLockoutException extends RuntimeException {

    private final long remainingLockoutSeconds;

    public AccountLockoutException(String message) {
        super(message);
        this.remainingLockoutSeconds = 0;
    }

    public AccountLockoutException(String message, long remainingLockoutSeconds) {
        super(message);
        this.remainingLockoutSeconds = remainingLockoutSeconds;
    }

    public long getRemainingLockoutSeconds() {
        return remainingLockoutSeconds;
    }
}
