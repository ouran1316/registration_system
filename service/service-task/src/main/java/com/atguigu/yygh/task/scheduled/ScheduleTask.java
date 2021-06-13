package com.atguigu.yygh.task.scheduled;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/6/2 14:55
 */
@Component
//开启定时任务
@EnableScheduling
public class ScheduleTask {

    @Autowired
    RabbitService rabbitService;

    //每天 8 点执行方法，就医提醒  0 0 8 * * ?
    // 0/30 * * * * ? 30秒一次 测试
    //cron表达式，设置执行间隔
    @Scheduled(cron = "0 0 8 * * ?")
    public void taskPatient() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
