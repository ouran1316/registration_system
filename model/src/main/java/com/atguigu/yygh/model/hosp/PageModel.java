package com.atguigu.yygh.model.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/28 14:23
 * 分页模型
 */
@Data
@ApiModel(description = "分页模型")
public class PageModel<T> {

    @ApiModelProperty(value = "当前页码")
    private int pageNum;

    @ApiModelProperty(value = "查询总数")
    private int totalElements;

    @ApiModelProperty(value = "数据")
    private List<T> content;
}
