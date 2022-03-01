package com.atguigu.yygh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/10 17:42
 */
@Data
@ApiModel(description = "ScheduleResponse")
@Document("ScheduleResponse")
public class ScheduleResponse<T> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "通用数据")
    public T data;
}

