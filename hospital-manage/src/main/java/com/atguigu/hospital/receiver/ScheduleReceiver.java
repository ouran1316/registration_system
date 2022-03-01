package com.atguigu.hospital.receiver;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import com.atguigu.hospital.mapper.ScheduleMapper;
import com.atguigu.hospital.model.Schedule;
import com.atguigu.hospital.service.ApiService;
import com.atguigu.hospital.util.BeanUtils;
import com.atguigu.hospital.util.HttpRequestHelper;
import com.atguigu.hospital.util.YyghException;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/24 17:54
 * 处理消息队列中的更新Schedule任务
 */
@Component
public class ScheduleReceiver {

    @Resource
    ScheduleMapper scheduleMapper;

    @Autowired
    RabbitService rabbitService;

    @Autowired
    ApiService apiService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_UPDATE_SCHEDULE_8, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_UPDATE_SCHEDULE),
            key = {MqConst.ROUTING_UPDATE_SCHEDULE_8}
    ))
    public void receiver(String workDate, Message message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            List<Schedule> schedules = scheduleMapper.getSchedulesByWorkDate(workDate);
            for (Schedule schedule : schedules) {
                Schedule newSchedule = new Schedule();
                BeanUtils.copyBean(schedule, newSchedule, Schedule.class);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(newSchedule.getWorkDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // 给workDate增加7日
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                Date date1 = calendar.getTime();
                String workDate1 = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                // 更新日期和可用数量
                newSchedule.setWorkDate(workDate1);
                newSchedule.setAvailableNumber(1);
                scheduleMapper.insert(newSchedule);

                // 更新 mongoDB schedule表
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("id", newSchedule.getId());
                paramMap.put("hoscode",newSchedule.getHoscode());
                paramMap.put("depcode",newSchedule.getDepcode());
                paramMap.put("title",newSchedule.getTitle());
                paramMap.put("docname",newSchedule.getDocname());
                paramMap.put("skill", newSchedule.getSkill());
                paramMap.put("workDate",newSchedule.getWorkDate());
                paramMap.put("workTime", newSchedule.getWorkTime());
                paramMap.put("reservedNumber",newSchedule.getReservedNumber());
                paramMap.put("availableNumber",newSchedule.getAvailableNumber());
                paramMap.put("amount",newSchedule.getAmount());
                paramMap.put("status",newSchedule.getStatus());
                paramMap.put("hosScheduleId", schedule.getId());
                paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
                paramMap.put("sign",HttpRequestHelper.getSign(paramMap, apiService.getSignKey()));

                JSONObject respone = HttpRequestHelper.sendRequest(paramMap,
                        apiService.getApiUrl()+"/api/hosp/saveSchedule");
                if(null == respone || 200 != respone.getIntValue("code")) {
                    throw new YyghException(respone.getString("message"), 201);
                }
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }
}
