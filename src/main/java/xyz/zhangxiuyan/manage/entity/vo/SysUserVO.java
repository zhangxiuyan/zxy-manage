package xyz.zhangxiuyan.manage.entity.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zxy
 * @version 1.0 - 2023/6/12
 */
public class SysUserVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;

    private String account;

    private String userPassword;

    private String userName;

    private String nickname;

    private String mobile;

    private String avatar;

    private LocalDateTime createTime;
    
    private String role;

    public SysUserVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
}
