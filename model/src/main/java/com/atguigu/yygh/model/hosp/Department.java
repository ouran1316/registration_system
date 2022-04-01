package com.atguigu.yygh.model.hosp;

import com.atguigu.yygh.model.base.BaseMongoEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * Department
 * </p>
 *
 * @author qy
 */
@Data
@ApiModel(description = "Department")
@Document("Department")
public class Department extends BaseMongoEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "单位编号")
	@Indexed //普通索引
	private String hoscode;

	@ApiModelProperty(value = "场地编号")
	@Indexed(unique = true) //唯一索引
	private String depcode;

	@ApiModelProperty(value = "场地名称")
	private String depname;

	@ApiModelProperty(value = "场地描述")
	private String intro;

	@ApiModelProperty(value = "大场地编号")
	private String bigcode;

	@ApiModelProperty(value = "大场地名称")
	private String bigname;

}

