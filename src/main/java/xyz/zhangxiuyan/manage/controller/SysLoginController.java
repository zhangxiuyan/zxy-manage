package xyz.zhangxiuyan.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.dto.LoginRequestDTO;
import xyz.zhangxiuyan.manage.entity.dto.RefreshTokenRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.LoginResponseVO;
import xyz.zhangxiuyan.manage.service.SysLoginService;

/**
 * @author zxy
 * @version 1.0 - 2025/10/20
 */
@Tag(name = "系统模块-登录")
@RestController
@RequestMapping("/sys/login")
public class SysLoginController {

    @Resource
    private SysLoginService sysLoginService;

    @Operation(summary = "登录")
    @PostMapping
    public HttpResult<LoginResponseVO> userLogin(@Valid @RequestBody LoginRequestDTO loginRequest,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        return HttpResult.success(sysLoginService.userLogin(loginRequest, request, response));
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public HttpResult<LoginResponseVO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request,
                                                    HttpServletRequest httpRequest,
                                                    HttpServletResponse httpResponse) {
        return HttpResult.success(sysLoginService.refreshToken(request.getRefreshToken(), httpRequest, httpResponse));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public HttpResult<Void> logout(@RequestBody LogoutRequestDTO request,
                                   HttpServletRequest httpRequest) {
        String accessToken = extractAccessToken(httpRequest);
        sysLoginService.logout(accessToken, request.getRefreshToken());
        return HttpResult.success(null);
    }

    @Operation(summary = "是否开启双重验证")
    @GetMapping("/second-step-code")
    public HttpResult<String> secondStepCode() {
        return HttpResult.success(sysLoginService.secondStepCode());
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 登出请求DTO
     */
    public static class LogoutRequestDTO {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

}
