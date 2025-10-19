package xyz.zhangxiuyan.manage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.service.SysLoginService;
import xyz.zhangxiuyan.manage.service.SysUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author zxy
 * @version 1.0 - 2023/8/30
 */
@Api(tags = "系统模块-登录")
@RestController
@RequestMapping("/system/auth")
public class SysLoginController {

    @Resource
    private SysLoginService sysLoginService;


    @ApiOperation("登录")
    @PostMapping(value = "/login")
    public HttpResult<Map<String, String>> userLogin(@RequestBody SysUser sysUser, HttpServletResponse response) {
        return HttpResult.success(sysLoginService.userLogin(sysUser, response));
    }

    @ApiOperation("是否开启双重验证")
    @GetMapping(value = "/second-step-code")
    public HttpResult<String> secondStepCode() {
        return HttpResult.success(sysLoginService.secondStepCode());
    }

}
