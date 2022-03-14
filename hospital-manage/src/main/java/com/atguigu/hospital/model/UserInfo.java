package com.atguigu.hospital.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 9:34
 * 用户信息（登陆页面接收信息）
 */
@Data
@ApiModel(description = "管理员账号信息")
@TableName("user_info")
public class UserInfo extends BaseNoAutoEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员账号名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "管理员密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "管理员绑定单位码")
    private String hoscode;
}
