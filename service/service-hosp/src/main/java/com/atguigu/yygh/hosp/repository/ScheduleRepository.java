package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 16:02
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);

    // 根据医院编号 、科室编号、工作日期和场地号，查询排班详细信息
    public List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDateAndDocname(
            String hoscode, String depcode, Date toDate, String docname);

    //根据 hosScheduleId 查询科室信息
    Schedule getScheduleByHosScheduleId(String hosScheduleId);

    // 根据 hoscode depcode workDate 分页查询场地信息
    public List<Schedule> findDistinctByHoscodeAndDepcodeAndWorkDate (
            String hoscode, String depcode, Date workDate);

}
