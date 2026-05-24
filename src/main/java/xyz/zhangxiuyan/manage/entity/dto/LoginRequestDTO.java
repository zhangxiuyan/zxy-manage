package xyz.zhangxiuyan.manage.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request DTO - supports accountId, email, or mobile + password
 */
@Data
public class LoginRequestDTO {

    @NotBlank(message = "{business.user.principal.required}")
    private String principal;

    @NotBlank(message = "{business.user.password.required}")
    private String password;

}
