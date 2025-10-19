package xyz.zhangxiuyan.manage.entity.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2024/7/18
 */
public class SysRouterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String routerPath;

    private String routerName;

    private String routerKey;

    private String component;

    private String redirect;

    private Long parentId;

    private String icon;

    private Integer keepAlive;

    private Integer sort;

    private Long creatorId;

    private SysRouterMetaVO meta;

    private List<SysRouterVO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRouterPath() {
        return routerPath;
    }

    public void setRouterPath(String routerPath) {
        this.routerPath = routerPath;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public String getRouterKey() {
        return routerKey;
    }

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public SysRouterMetaVO getMeta() {
        return meta;
    }

    public void setMeta(SysRouterMetaVO meta) {
        this.meta = meta;
    }

    public List<SysRouterVO> getChildren() {
        return children;
    }

    public void setChildren(List<SysRouterVO> children) {
        this.children = children;
    }

}
