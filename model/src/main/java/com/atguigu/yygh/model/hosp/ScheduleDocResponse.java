package com.atguigu.yygh.model.hosp;

import com.atguigu.yygh.ScheduleResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/11 18:12
 * 场地名称分页显示
 */
@Data
public class ScheduleDocResponse {

    @ApiModelProperty(value = "场地名称分页结果")
    private List<Schedule> scheduleDocList;

    @ApiModelProperty(value = "总页数")
    private Integer total;

    @ApiModelProperty(value = "当前页数")
    private Integer currentPage;

    @ApiModelProperty(value = "每页数量")
    private Integer limit;
}
