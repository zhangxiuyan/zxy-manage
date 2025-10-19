package xyz.zhangxiuyan.manage.entity.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2024/8/5
 */
public class SysRouterMetaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Boolean keepAlive;

    private String icon;

    private List<String> permission;

    public SysRouterMetaVO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }

}
