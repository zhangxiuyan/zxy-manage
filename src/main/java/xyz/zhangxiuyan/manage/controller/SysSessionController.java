package xyz.zhangxiuyan.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.LoginUser;
import xyz.zhangxiuyan.manage.entity.vo.SessionVO;
import xyz.zhangxiuyan.manage.service.SessionManagementService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话管理控制器
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Tag(name = "系统模块-会话管理")
@RestController
@RequestMapping("/sys/sessions")
public class SysSessionController {

    @Resource
    private SessionManagementService sessionManagementService;

    @Operation(summary = "获取当前用户的会话列表")
    @GetMapping
    public HttpResult<Map<String, Object>> getSessions() {
        LoginUser user = getCurrentUser();
        List<SessionVO> sessions = sessionManagementService.getUserSessions(user.getId());
        int maxConcurrent = sessionManagementService.getMaxConcurrentSessions(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("sessions", sessions);
        result.put("maxConcurrent", maxConcurrent);
        result.put("currentCount", sessions.size());

        return HttpResult.success(result);
    }

    @Operation(summary = "终止指定会话")
    @DeleteMapping("/{sessionId}")
    public HttpResult<Void> terminateSession(@PathVariable Long sessionId) {
        LoginUser user = getCurrentUser();
        sessionManagementService.terminateSession(user.getId(), sessionId);
        return HttpResult.success(null);
    }

    @Operation(summary = "终止所有其他会话")
    @DeleteMapping
    public HttpResult<Map<String, Integer>> terminateAllOtherSessions(
            @RequestParam(required = false) String excludeTokenHash) {
        LoginUser user = getCurrentUser();
        int count = sessionManagementService.terminateAllOtherSessions(user.getId(), excludeTokenHash);

        Map<String, Integer> result = new HashMap<>();
        result.put("terminatedCount", count);
        return HttpResult.success(result);
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (LoginUser) authentication.getPrincipal();
    }
}
