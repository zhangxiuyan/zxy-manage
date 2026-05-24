package xyz.zhangxiuyan.manage.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequestDTO {

    @Size(max = 128, message = "{business.user.nickname.size}")
    private String nickname;

    @Email(message = "{business.user.email.invalid.format}")
    @Size(max = 100, message = "{business.user.email.size}")
    private String email;

    @Pattern(regexp = "^[0-9]{0,20}$", message = "{business.user.mobile.invalid.format}")
    private String mobile;
}
