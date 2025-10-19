package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.common.exception.BusinessException;
import xyz.zhangxiuyan.common.utils.AESUtil;
import xyz.zhangxiuyan.common.utils.JWTUtil;
import xyz.zhangxiuyan.manage.common.Constants.SystemConstant;
import xyz.zhangxiuyan.manage.common.Enums.SystemParameterEnum;
import xyz.zhangxiuyan.manage.entity.SysParameter;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.service.SysLoginService;
import xyz.zhangxiuyan.manage.service.SysParameterService;
import xyz.zhangxiuyan.manage.service.SysUserService;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxy
 * @version 1.0 - 2023/9/1
 */
@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysParameterService sysParameterService;

    @Override
    public Map<String, String> userLogin(SysUser sysUser, HttpServletResponse response) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getEmail, sysUser.getEmail());

        SysUser user = sysUserService.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("user does not exist");
        }

        if (!user.getPassword().equals(sysUser.getPassword())) {
            throw new BusinessException("Wrong Password");
        }
       
        Map<String, String> map = new HashMap<>(); //用来存放payload信息
        map.put("id", user.getId().toString());
        map.put("userName", user.getUsername());
        // map.put("role",one.getRole());
        // 生成token令牌
        String jwt = JWTUtil.generateToken(map);
//        redisTemplate
        Cookie cookie = new Cookie(SystemConstant.AUTHORIZATION, jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(SystemConstant.COOKIE_MAX_AGE);
        response.addCookie(cookie);
        response.setHeader(SystemConstant.AUTHORIZATION, jwt);
        String token;
        try {
            token = AESUtil.encrypt(jwt);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(SystemConstant.TOKEN, token);
        return hashMap;
    }

    @Override
    public String secondStepCode() {
        SysParameter parameter = sysParameterService.getOne(new LambdaQueryWrapper<SysParameter>().eq(SysParameter::getParameterDeleteMark, SystemConstant.DEFAULT_DELETE_FLAG)
                .eq(SysParameter::getParameterKey, SystemConstant.SECOND_STEP_CODE));
        if (parameter == null) {
            return SystemParameterEnum.OFF.getValue();
        }
        return parameter.getParameterValue();
    }

}
