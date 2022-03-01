package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 9:58
 */
public interface OrderService extends IService<OrderInfo> {

    //创建订单
    Long saveOrder(String scheduleId, Long userId, String name, String phone, Integer sex);

    //根据订单id查询订单详情
    OrderInfo getOrder(String orderId);

    /**
     * 条件查询用户预约订单
     */
    public List<OrderInfo> getUserOrders(OrderCountQueryVo orderCountQueryVo);

    /**
     * 取消预约
     */
    Boolean cancelOrder(Long orderId);

    //就诊通知
    void patientTips();

    //预约统计
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
