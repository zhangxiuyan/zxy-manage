package xyz.zhangxiuyan.manage.exception;

public class BusinessException extends RuntimeException {

    private final String code;
    private final String defaultMessage;

    public BusinessException(String message) {
        super(message);
        this.code = null;
        this.defaultMessage = message;
    }

    public BusinessException(String code, String defaultMessage) {
        super(defaultMessage);
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
