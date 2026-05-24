package xyz.zhangxiuyan.manage.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
}
