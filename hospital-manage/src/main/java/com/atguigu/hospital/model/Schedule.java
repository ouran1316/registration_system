package com.atguigu.hospital.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Schedule
 * </p>
 *
 * @author qy
 */
@Data
@ApiModel(description = "Schedule")
@TableName("schedule")
public class Schedule extends BaseNoAutoEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "单位编号")
	@TableField("hoscode")
	private String hoscode;

	@ApiModelProperty(value = "场地编号")
	@TableField("depcode")
	private String depcode;

	@ApiModelProperty(value = "场馆")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "场地号")
	@TableField("docname")
	private String docname;

	@ApiModelProperty(value = "预约时长")
	@TableField("skill")
	private String skill;

	@ApiModelProperty(value = "安排日期")
	@TableField("work_date")
	private String workDate;

	@ApiModelProperty(value = "安排时间（0：上午 1：下午）")
	@TableField("work_time")
	private Integer workTime;

	@ApiModelProperty(value = "可预约数")
	@TableField("reserved_number")
	private Integer reservedNumber;

	@ApiModelProperty(value = "剩余预约数")
	@TableField("available_number")
	private Integer availableNumber;

	@ApiModelProperty(value = "费用")
	@TableField("amount")
	private String amount;

	@ApiModelProperty(value = "排期状态（-1：停诊 0：停约 1：可约）")
	@TableField("status")
	private Integer status;
}

