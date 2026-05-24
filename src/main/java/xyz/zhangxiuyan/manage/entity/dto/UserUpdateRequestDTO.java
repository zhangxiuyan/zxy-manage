package xyz.zhangxiuyan.manage.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * User update request DTO
 */
public class UserUpdateRequestDTO {

    @Size(max = 128, message = "{business.user.nickname.size}")
    private String nickname;

    @Email(message = "{business.user.email.invalid.format}")
    @Size(max = 100, message = "{business.user.email.size}")
    private String email;

    @Pattern(regexp = "^[0-9]{0,20}$", message = "{business.user.mobile.invalid.format}")
    private String mobile;

    public UserUpdateRequestDTO() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
