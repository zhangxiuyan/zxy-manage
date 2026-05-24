package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_parameter")
public class SysParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long parameterId;

    private String parameterName;

    private String parameterKey;

    private String parameterValue;

    private String parameterMark;

    private LocalDateTime parameterCreateTime;

    private Long parameterCreateUser;

    private LocalDateTime parameterUpdateTime;

    private Long parameterUpdateUser;

    private Short parameterDeleteMark;
}
