package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.ScheduleCommonRequest;
import com.atguigu.yygh.ScheduleResponse;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.model.hosp.ScheduleDocResponse;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 16:03
 */
public interface ScheduleService {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    //查询排班接口
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //删除排班
    void remove(String hoscode, String hosScheduleId);

    //根据医院编号和科室编号，查询排班规则数据
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    // 根据医院编号 、科室编号、工作日期和场地号，查询排班详细信息
    public List<Schedule> getDetailSchedule2(String hoscode, String depcode, String workDate, String docName);

    //获取可预约排班数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    //根据排班id获取排班详细数据
    Schedule getScheduleId(String scheduleId);

    // 根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据 用于 mq
    void update(Schedule schedule);

    // 获取指定日期可用球场
    public ScheduleResponse<ScheduleDocResponse> getDocName(ScheduleCommonRequest request);
}
