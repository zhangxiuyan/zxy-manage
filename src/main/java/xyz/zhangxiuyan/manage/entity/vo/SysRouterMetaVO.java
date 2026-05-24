package xyz.zhangxiuyan.manage.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SysRouterMetaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Boolean keepAlive;

    private String icon;

    private List<String> permission;
}
