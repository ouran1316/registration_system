package com.atguigu.yygh.msm.receiver;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
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
import java.util.List;

/**
 * mq 监听器
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/31 15:15
 */
@Component
public class MsmReceiver {

    @Autowired
    private MsmService msmService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void send(OrderMqVo orderMqVo, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        System.out.println("进入 send 方法");
        try {
            MsmVo msmVo = orderMqVo.getMsmVo();
            if(null != msmVo) {
                msmService.send(msmVo);
            }

            /**
             * 无异常就确认消息
             * basicAck(long deliveryTag, boolean multiple)
             * deliveryTag:取出来当前消息在队列中的的索引;
             * multiple:为true的话就是批量确认,如果当前deliveryTag为5,那么就会确认
             * deliveryTag为5及其以下的消息;一般设置为false
             */
            channel.basicAck(tag, false);
        } catch (Exception e) {
            /**
             * 有异常就绝收消息
             * basicNack(long deliveryTag, boolean multiple, boolean requeue)
             * requeue:true为将消息重返当前消息队列,还可以重新发送给消费者;
             *         false:将消息丢弃
             */
            channel.basicNack(tag,false,true);
        }

    }

}
