package com.atguigu.yygh.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/13 10:08
 * 披露到前端的用户信息
 */
@Data
@ApiModel(description = "UserInfoVo")
public class UserInfoVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号")
    @TableField("邮箱号")
    private String phone;

    @ApiModelProperty(value = "用户姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "状态（0：锁定 1：正常）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "用户密码 md5加密")
    @TableField("password")
    private String password;
}
