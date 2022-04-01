package com.atguigu.yygh.vo.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Department")
public class DepartmentQueryVo {
	
	@ApiModelProperty(value = "单位编号")
	private String hoscode;

	@ApiModelProperty(value = "场地编号")
	private String depcode;

	@ApiModelProperty(value = "场地")
	private String depname;

	@ApiModelProperty(value = "大场地编号")
	private String bigcode;

	@ApiModelProperty(value = "单位")
	private String bigname;

}

