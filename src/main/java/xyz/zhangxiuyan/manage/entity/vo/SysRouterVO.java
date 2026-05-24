package xyz.zhangxiuyan.manage.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
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
}
