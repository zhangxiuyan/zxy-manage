package xyz.zhangxiuyan.manage.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "{business.auth.refresh.token.required}")
    private String refreshToken;
}
