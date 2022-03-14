package com.atguigu.hospital.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 11:58
 */
@Data
@ApiModel(description = "管理员账号登陆信息")
public class UserInfoVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员账号名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "管理员密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "管理员绑定单位码")
    private String hoscode;

    @ApiModelProperty(value = "验证码")
    private String code;
}
