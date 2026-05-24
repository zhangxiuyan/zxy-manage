package xyz.zhangxiuyan.manage.exception;

/**
 * 速率限制异常
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }
}
