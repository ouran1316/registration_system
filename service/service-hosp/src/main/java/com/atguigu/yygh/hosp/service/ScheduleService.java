package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.ScheduleCommonRequest;
import com.atguigu.yygh.ScheduleResponse;
import com.atguigu.yygh.model.hosp.PageModel;
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
    //上传排期接口
    void save(Map<String, Object> paramMap);


//    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);
    /**
     * 查询排期接口
     */
    PageModel<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //删除排期
    void remove(String hoscode, String hosScheduleId);

    //根据单位编号和场地编号，查询排期规则数据
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    //根据单位编号 、场地编号和工作日期，查询排期详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    // 根据单位编号 、场地编号、工作日期和场地号，查询排期详细信息
    public List<Schedule> getDetailSchedule2(String hoscode, String depcode, String workDate, String docName);

    //获取可预约排期数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    //根据排期id获取排期详细数据
    Schedule getScheduleId(String scheduleId);

    // 根据排期id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排期数据 用于 mq
    void update(Schedule schedule);

    /**
     * 更新排期部分数据，用于api
     * @param schedule
     */
    void updateSchedule(Schedule schedule);

    // 获取指定日期可用球场
    public ScheduleResponse<ScheduleDocResponse> getDocName(ScheduleCommonRequest request);
}
