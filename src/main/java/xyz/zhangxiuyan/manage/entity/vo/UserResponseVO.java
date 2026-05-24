package xyz.zhangxiuyan.manage.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.zhangxiuyan.manage.entity.SysUser;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponseVO {

    private Long id;
    private String accountId;
    private String nickname;
    private String email;
    private String mobile;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;

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
}
