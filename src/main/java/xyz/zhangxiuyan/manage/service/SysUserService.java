package xyz.zhangxiuyan.manage.service;

import org.springframework.security.core.userdetails.UserDetails;
import xyz.zhangxiuyan.manage.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.zhangxiuyan.manage.entity.dto.RegisterRequestDTO;
import xyz.zhangxiuyan.manage.entity.dto.UserUpdateRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.UserResponseVO;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;

import jakarta.servlet.http.HttpServletRequest;

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

    Long register(RegisterRequestDTO request);

    void updateUserInfo(UserUpdateRequestDTO request, Long userId);

    UserResponseVO getUserInfoById(Long userId);

    boolean isAccountIdExists(String accountId);

    boolean isNicknameExists(String nickname);

    boolean isEmailExists(String email);

    boolean isMobileExists(String mobile);

    /**
     * 根据用户名加载用户详情 (用于Spring Security)
     *
     * @param username 用户名
     * @return UserDetails
     */
    UserDetails loadUserByUsername(String username);

}
