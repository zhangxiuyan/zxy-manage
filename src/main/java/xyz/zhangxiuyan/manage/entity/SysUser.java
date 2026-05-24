package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("account_id")
    private String accountId;

    @TableField("email")
    private String email;

    @TableField("password")
    private String password;

    @TableField("username")
    private String username;

    @TableField("nickname")
    private String nickname;

    @TableField("mobile")
    private String mobile;

    @TableField("avatar")
    private String avatar;

    @TableField("wx_openid")
    private String wxOpenid;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("delete_flag")
    private Integer deleteFlag;

    @TableField("status")
    private Integer status;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("created_by")
    private Long createdBy;

    @TableField("role_id")
    private Long roleId;
}
