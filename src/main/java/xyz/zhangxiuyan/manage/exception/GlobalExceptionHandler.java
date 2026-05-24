package xyz.zhangxiuyan.manage.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.zhangxiuyan.common.http.HttpResult;

import javax.annotation.Resource;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpResult<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::getLocalizedMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return HttpResult.error(message, null);
    }

    @ExceptionHandler(BusinessException.class)
    public HttpResult<Void> handleBusinessException(BusinessException ex) {
        String message = getLocalizedMessage(ex.getCode(), ex.getDefaultMessage());
        log.warn("Business error: {}", message);
        return HttpResult.error(message, null);
    }

    @ExceptionHandler(AccountLockoutException.class)
    public HttpResult<Void> handleAccountLockoutException(AccountLockoutException ex) {
        log.warn("Account locked: {}", ex.getMessage());
        return HttpResult.error(ex.getMessage(), null);
    }

    @ExceptionHandler(RateLimitException.class)
    public HttpResult<Void> handleRateLimitException(RateLimitException ex) {
        log.warn("Rate limited: {}", ex.getMessage());
        return HttpResult.error(ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public HttpResult<Void> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        return HttpResult.error("Internal server error", null);
    }

    private String getLocalizedMessage(FieldError error) {
        String code = error.getDefaultMessage();
        if (code != null) {
            // Strip {} if message code is wrapped like {business.user.accountId.required}
            if (code.startsWith("{") && code.endsWith("}")) {
                code = code.substring(1, code.length() - 1);
            }
            try {
                return messageSource.getMessage(code, error.getArguments(), LocaleContextHolder.getLocale());
            } catch (Exception e) {
                // Fallback to default message
            }
        }
        return error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error";
    }

    private String getLocalizedMessage(String code, String defaultMessage) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultMessage;
        }
    }
}
