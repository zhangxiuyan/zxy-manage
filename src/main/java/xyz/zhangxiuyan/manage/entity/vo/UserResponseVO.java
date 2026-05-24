package xyz.zhangxiuyan.manage.entity.vo;

import xyz.zhangxiuyan.manage.entity.SysUser;

import java.time.LocalDateTime;

/**
 * User response VO
 */
public class UserResponseVO {

    private Long id;
    private String accountId;
    private String nickname;
    private String email;
    private String mobile;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;

    public UserResponseVO() {
    }

    public static UserResponseVO from(SysUser user) {
        UserResponseVO response = new UserResponseVO();
        response.setId(user.getId());
        response.setAccountId(user.getAccountId());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setMobile(user.getMobile());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
