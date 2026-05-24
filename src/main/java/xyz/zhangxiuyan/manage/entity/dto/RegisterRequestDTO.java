package xyz.zhangxiuyan.manage.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
}
