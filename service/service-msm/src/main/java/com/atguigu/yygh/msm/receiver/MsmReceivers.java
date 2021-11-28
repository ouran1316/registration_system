package com.atguigu.yygh.msm.receiver;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.vo.msm.MsmVo;
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

import java.io.IOException;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/7/19 23:12
 */

@Component
public class MsmReceivers {

    @Autowired
    private MsmService msmService;

    //接收定时预约提醒
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM2, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send2(MsmVo[] msmVos, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            if(msmVos.length > 0) {
                msmService.sends(msmVos);
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag,false,true);
        }

    }
}
