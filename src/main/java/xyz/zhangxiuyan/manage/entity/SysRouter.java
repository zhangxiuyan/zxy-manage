package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_router")
public class SysRouter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Short deleteMark;
}
