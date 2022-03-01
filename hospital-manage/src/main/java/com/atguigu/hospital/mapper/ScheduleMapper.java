package com.atguigu.hospital.mapper;

import com.atguigu.hospital.model.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("select * from schedule where work_date = '${work_date}' ;")
    List<Schedule> getSchedulesByWorkDate(@Param("work_date") String workDate);
}
