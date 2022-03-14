package com.atguigu.hospital.mapper;

import com.atguigu.hospital.model.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 11:06
 * 管理员信息数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
}
