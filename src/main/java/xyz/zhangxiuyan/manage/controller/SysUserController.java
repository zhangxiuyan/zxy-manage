package xyz.zhangxiuyan.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.dto.RegisterRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.UserResponseVO;
import xyz.zhangxiuyan.manage.entity.dto.UserUpdateRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;
import xyz.zhangxiuyan.manage.service.SysUserService;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 系统模块-用户管理 前端控制器
 * </p>
 *
 * @author zxy
 * @since 2023-06-12
 */
@Tag(name = "系统模块-用户管理")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public HttpResult<Map<String, Long>> register(@Valid @RequestBody RegisterRequestDTO request) {
        Long userId = sysUserService.register(request);
        Map<String, Long> data = new HashMap<>();
        data.put("userId", userId);
        return HttpResult.success(data);
    }

    @Operation(summary = "新增用户")
    @PostMapping("/create")
    public HttpResult<String> create(@RequestBody SysUser sysUser) {
        sysUserService.create(sysUser);
        return HttpResult.success("success");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public HttpResult<UserResponseVO> queryUserInfo(HttpServletRequest request) {
        // Reuse existing token-based user info retrieval
        SysUserVO vo = sysUserService.queryUserInfo(request);
        // Convert VO to Response VO
        UserResponseVO response = new UserResponseVO();
        response.setId(vo.getId());
        response.setAccountId(vo.getAccount());
        response.setNickname(vo.getNickname());
        response.setAvatar(vo.getAvatar());
        // Note: email/mobile not in VO, would need to query by id for full info
        return HttpResult.success(response);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public HttpResult<Void> updateUserInfo(
            @Valid @RequestBody UserUpdateRequestDTO request,
            HttpServletRequest httpRequest) {
        // Extract userId from token (reuse existing logic)
        SysUserVO vo = sysUserService.queryUserInfo(httpRequest);
        sysUserService.updateUserInfo(request, vo.getId());
        return HttpResult.success(null);
    }

}
