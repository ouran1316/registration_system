package com.atguigu.yygh.order.client;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.mapstruct.Mapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/6/2 18:16
 */
@FeignClient(value = "service-order")
@Mapper
public interface OrderFeignClient {
    /**
     * 获取订单统计数据
     */
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);

    //根据订单id查询订单详情
    @GetMapping("auth/getOrders/{orderId}")
    Result getOrders(@PathVariable String orderId);
}
