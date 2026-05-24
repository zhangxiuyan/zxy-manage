package xyz.zhangxiuyan.manage.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysUserDTO extends CommonDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
}
