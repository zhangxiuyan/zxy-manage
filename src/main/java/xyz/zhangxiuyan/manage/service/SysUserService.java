package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxy
 * @since 2023-06-12
 */
public interface SysUserService extends IService<SysUser> {

    void create(SysUser sysUser);

    SysUserVO queryUserInfo(HttpServletRequest request);
    
}
