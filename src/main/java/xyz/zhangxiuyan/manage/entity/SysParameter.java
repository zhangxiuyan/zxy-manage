package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zxy
 * @since 2023-09-11
 */
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

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterMark() {
        return parameterMark;
    }

    public void setParameterMark(String parameterMark) {
        this.parameterMark = parameterMark;
    }

    public LocalDateTime getParameterCreateTime() {
        return parameterCreateTime;
    }

    public void setParameterCreateTime(LocalDateTime parameterCreateTime) {
        this.parameterCreateTime = parameterCreateTime;
    }

    public Long getParameterCreateUser() {
        return parameterCreateUser;
    }

    public void setParameterCreateUser(Long parameterCreateUser) {
        this.parameterCreateUser = parameterCreateUser;
    }

    public LocalDateTime getParameterUpdateTime() {
        return parameterUpdateTime;
    }

    public void setParameterUpdateTime(LocalDateTime parameterUpdateTime) {
        this.parameterUpdateTime = parameterUpdateTime;
    }

    public Long getParameterUpdateUser() {
        return parameterUpdateUser;
    }

    public void setParameterUpdateUser(Long parameterUpdateUser) {
        this.parameterUpdateUser = parameterUpdateUser;
    }

    public Short getParameterDeleteMark() {
        return parameterDeleteMark;
    }

    public void setParameterDeleteMark(Short parameterDeleteMark) {
        this.parameterDeleteMark = parameterDeleteMark;
    }

    @Override
    public String toString() {
        return "SysParameter{" +
            "parameterId = " + parameterId +
            ", parameterName = " + parameterName +
            ", parameterKey = " + parameterKey +
            ", parameterValue = " + parameterValue +
            ", parameterMark = " + parameterMark +
            ", parameterCreateTime = " + parameterCreateTime +
            ", parameterCreateUser = " + parameterCreateUser +
            ", parameterUpdateTime = " + parameterUpdateTime +
            ", parameterUpdateUser = " + parameterUpdateUser +
            ", parameterDeleteMark = " + parameterDeleteMark +
        "}";
    }
}
