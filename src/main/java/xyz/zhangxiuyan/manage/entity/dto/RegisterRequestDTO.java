package xyz.zhangxiuyan.manage.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * User registration request DTO
 */
public class RegisterRequestDTO {

    @NotBlank(message = "{business.user.accountId.required}")
    @Size(max = 128, message = "{business.user.accountId.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{business.user.accountId.invalid.format}")
    private String accountId;

    @NotBlank(message = "{business.user.nickname.required}")
    @Size(max = 128, message = "{business.user.nickname.size}")
    private String nickname;

    @NotBlank(message = "{business.user.password.required}")
    private String password;

    public RegisterRequestDTO() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
