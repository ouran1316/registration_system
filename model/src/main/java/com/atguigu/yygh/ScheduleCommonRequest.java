package com.atguigu.yygh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/10 17:47
 */
@ApiModel(description = "ScheduleCommonRequest")
@Document("ScheduleCommonRequest")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommonRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页数")
    private Integer page;

    @ApiModelProperty(value = "每页限制")
    private Integer limit;

    @ApiModelProperty(value = "学校编号")
    private String hosode;

    @ApiModelProperty(value = "场地编号")
    private String depcode;

    @ApiModelProperty(value = "时间")
    private String date;
}
