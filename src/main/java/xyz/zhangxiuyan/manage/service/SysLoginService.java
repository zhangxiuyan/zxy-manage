package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.dto.LoginRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.LoginResponseVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface SysLoginService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request      HTTP请求
     * @param response     HTTP响应
     * @return 登录响应
     */
    LoginResponseVO userLogin(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @param request      HTTP请求
     * @param response     HTTP响应
     * @return 新的登录响应
     */
    LoginResponseVO refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    /**
     * 用户登出
     *
     * @param accessToken  访问令牌
     * @param refreshToken 刷新令牌
     */
    void logout(String accessToken, String refreshToken);

    /**
     * 检查是否开启双重验证
     *
     * @return ON/OFF
     */
    String secondStepCode();
}
