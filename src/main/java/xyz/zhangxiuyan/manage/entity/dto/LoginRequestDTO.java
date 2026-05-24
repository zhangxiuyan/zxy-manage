package xyz.zhangxiuyan.manage.entity.dto;

import javax.validation.constraints.NotBlank;

/**
 * Login request DTO - supports accountId, email, or mobile + password
 */
public class LoginRequestDTO {

    @NotBlank(message = "{business.user.principal.required}")
    private String principal;

    @NotBlank(message = "{business.user.password.required}")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String principal, String password) {
        this.principal = principal;
        this.password = password;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
