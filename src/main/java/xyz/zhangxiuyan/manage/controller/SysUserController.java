package xyz.zhangxiuyan.manage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;
import xyz.zhangxiuyan.manage.service.SysUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zxy
 * @since 2023-06-12
 */
@Api(tags = "系统模块-用户管理")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @ApiOperation("新增用户")
    @PostMapping("/create")
    public HttpResult<String> create(@RequestBody SysUser sysUser) {
        sysUserService.create(sysUser);
        return HttpResult.success("success");
    }

    @GetMapping("/info")
    public HttpResult<SysUserVO> queryUserInfo(HttpServletRequest request) {
        return HttpResult.success(sysUserService.queryUserInfo(request));
    }
    
}
