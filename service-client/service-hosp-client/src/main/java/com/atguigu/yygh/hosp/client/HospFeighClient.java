package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 11:13
 */
@FeignClient(value = "service-hosp")
@Repository
public interface HospFeighClient {

    /**
     * 根据排期id获取预约下单数据
     * @param scheduleId
     * @return
     */
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

    /**
     * 根据排期id获取排期详细数据
     * @param scheduleId
     * @return
     */
    @GetMapping("/api/hosp/hospital/getSchedule/{scheduleId}")
    public Result<Schedule> getSchedule(@PathVariable("scheduleId") String scheduleId);

    /**
     * 获取单位签名信息
     * @param hoscode
     * @return
     */
    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);

}
