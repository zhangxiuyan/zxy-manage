package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.SysUser;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface SysLoginService {

    Map<String, String> userLogin(SysUser sysUser, HttpServletResponse response);

    String secondStepCode();

}
