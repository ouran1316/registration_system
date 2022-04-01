package com.atguigu.yygh.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "OrderCountQueryVo")
public class OrderCountQueryVo {
	
	@ApiModelProperty(value = "单位编号")
	private String hoscode;

	@ApiModelProperty(value = "单位名称")
	private String hosname;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "查询起始日期")
	private String reserveDateBegin;

	@ApiModelProperty(value = "查询结束日志")
	private String reserveDateEnd;

}

