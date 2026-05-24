package xyz.zhangxiuyan.manage.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2025/10/20
 */
@Getter
@Setter
public class LoginUser implements UserDetails, Serializable {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    /** 是否启用 */
    private Boolean enabled;

    /** 是否锁定 */
    private Boolean locked;

    /** 权限信息 — Lombok @Getter 自动生成协变返回类型，满足 UserDetails 接口 */
    private List<GrantedAuthority> authorities;

    private String deptId;

    private String avatarUrl;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == null || !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }
}
