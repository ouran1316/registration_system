package com.atguigu.common.rabbit.constant;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/31 15:04
 */
public class MqConst {

    /**
     * 预约下单
     */
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    public static final String ROUTING_ORDER = "order";
    //队列
    public static final String QUEUE_ORDER  = "queue.order";

    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_MSM = "exchange.direct.msm";
    public static final String ROUTING_MSM_ITEM = "msm.item";
    //队列
    public static final String QUEUE_MSM_ITEM  = "queue.msm.item";

    /**
     * 定时任务，提醒预约
     */
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
    //队列
    public static final String QUEUE_TASK_8 = "queue.task.8";

}

