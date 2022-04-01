package com.atguigu.yygh.vo.hosp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HospitalSetQueryVo {

    @ApiModelProperty(value = "单位名称")
    private String hosname;

    @ApiModelProperty(value = "单位编号")
    private String hoscode;
}
