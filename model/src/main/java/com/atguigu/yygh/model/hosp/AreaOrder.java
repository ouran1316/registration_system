package com.atguigu.yygh.model.hosp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/1/28 16:14
 * 场地预约
 */
@Data
@ApiModel(description = "AreaOrder")
@Document("AreaOrder")
public class AreaOrder {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "医院编号，学校编号")
    // 唯一索引
    @Indexed(unique = true)
    private String hoscode;

    @ApiModelProperty(value = "医院名称，学校名称")
    // 普通索引
    @Indexed
    private String hosname;

    @ApiModelProperty(value = "职称，球类")
    private String title;

    @ApiModelProperty(value = "医生名称，球场名字")
    private String docname;

    @ApiModelProperty(value = "擅长技能，场地可用时间")
    private String skill;

    @ApiModelProperty(value = "排班日期，具体到日")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    private Date workDate;

    @ApiModelProperty(value = "排班时间（0：上午 1：下午）")
    private Integer workTime;

    @ApiModelProperty(value = "可预约数")
    private Integer reservedNumber;

    @ApiModelProperty(value = "剩余预约数")
    private Integer availableNumber;

    @ApiModelProperty(value = "挂号费，场地费")
    private BigDecimal amount;

    @ApiModelProperty(value = "排班状态（-1：场地维修 0：已约 1：可约）")
    private Integer status;

    @ApiModelProperty(value = "排班编号（医院自己的排班主键）")
    @Indexed //普通索引
    private String hosScheduleId;
}
