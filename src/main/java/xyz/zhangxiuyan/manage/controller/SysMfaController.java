package xyz.zhangxiuyan.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.LoginUser;
import xyz.zhangxiuyan.manage.entity.dto.MfaVerifyRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.MfaSetupResponseVO;
import xyz.zhangxiuyan.manage.service.MfaService;

/**
 * MFA控制器
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */

@Tag(name = "系统模块-MFA")
@RestController
@RequestMapping("/sys/mfa")
public class SysMfaController {

    @Resource
    private MfaService mfaService;

    @Operation(summary = "获取MFA配置")
    @GetMapping("/status")
    public HttpResult<Object> getMfaStatus() {
        LoginUser user = getCurrentUser();
        Object mfaInfo = mfaService.getUserMfa(user.getId());
        return HttpResult.success(mfaInfo);
    }

    @Operation(summary = "设置MFA (获取二维码)")
    @GetMapping("/setup")
    public HttpResult<MfaSetupResponseVO> setupMfa() {
        LoginUser user = getCurrentUser();
        MfaSetupResponseVO response = mfaService.setupMfa(user.getId(), user.getUsername());
        return HttpResult.success(response);
    }

    @Operation(summary = "启用MFA")
    @PostMapping("/enable")
    public HttpResult<Void> enableMfa(@Valid @RequestBody MfaVerifyRequestDTO request) {
        LoginUser user = getCurrentUser();
        boolean success = mfaService.enableMfa(user.getId(), request.getMfaToken());
        if (success) {
            return HttpResult.success(null);
        } else {
            return HttpResult.error("Invalid MFA code", null);
        }
    }

    @Operation(summary = "禁用MFA")
    @PostMapping("/disable")
    public HttpResult<Void> disableMfa(@Valid @RequestBody MfaVerifyRequestDTO request) {
        LoginUser user = getCurrentUser();
        boolean success = mfaService.disableMfa(user.getId(), request.getMfaToken());
        if (success) {
            return HttpResult.success(null);
        } else {
            return HttpResult.error("Invalid MFA code", null);
        }
    }

    @Operation(summary = "验证MFA验证码")
    @PostMapping("/verify")
    public HttpResult<Void> verifyMfa(@Valid @RequestBody MfaVerifyRequestDTO request) {
        LoginUser user = getCurrentUser();
        boolean valid;
        if (request.getMfaToken().length() == 10 && request.getMfaToken().matches("\\d+")) {
            // Backup code
            valid = mfaService.verifyBackupCode(user.getId(), request.getMfaToken());
        } else {
            // TOTP code
            valid = mfaService.verifyMfaCode(user.getId(), request.getMfaToken());
        }

        if (valid) {
            return HttpResult.success(null);
        } else {
            return HttpResult.error("Invalid MFA code", null);
        }
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (LoginUser) authentication.getPrincipal();
    }
}
