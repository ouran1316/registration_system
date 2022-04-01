package com.atguigu.yygh.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="登录对象")
public class LoginVo {

    @ApiModelProperty(value = "openid")
    private String openid;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "password")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;
}
