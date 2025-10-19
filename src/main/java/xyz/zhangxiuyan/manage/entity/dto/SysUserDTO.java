package xyz.zhangxiuyan.manage.entity.dto;

import java.io.Serializable;

/**
 * @author zxy
 * @version 1.0 - 2023/6/12
 */
public class SysUserDTO extends CommonDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    public SysUserDTO() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
