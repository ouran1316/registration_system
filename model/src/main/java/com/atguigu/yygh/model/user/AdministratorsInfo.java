package com.atguigu.yygh.model.user;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/11 10:00
 * 管理员
 */
@Data
@ApiModel(description = "AdministratorsInfo")
@TableName("administrators_info")
public class AdministratorsInfo extends BaseEntity {

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
