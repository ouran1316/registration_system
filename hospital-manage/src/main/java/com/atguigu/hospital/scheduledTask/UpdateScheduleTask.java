package com.atguigu.hospital.scheduledTask;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/24 17:37
 * 定时更新排期信息
 */
@Component
@EnableScheduling
public class UpdateScheduleTask {

    @Autowired
    RabbitService rabbitService;

    @Scheduled(cron = "20 52 09 * * ?")
    public void taskPatient() throws ParseException {
        // 每天晚上11点55分更新一次排期数据，转成mysql的时间格式
        String workDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_UPDATE_SCHEDULE, MqConst.ROUTING_UPDATE_SCHEDULE_8, workDate);
    }
}
