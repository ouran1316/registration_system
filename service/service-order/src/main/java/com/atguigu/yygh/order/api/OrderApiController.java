package com.atguigu.yygh.order.api;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 9:57
 */
@Api("订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
@Slf4j
public class OrderApiController {

    @Autowired
    OrderService orderService;

    //生成订单信息
    @ApiOperation(value = "创建订单")
    @PostMapping("/auth/submitOrder/{scheduleId}/{name}/{phone}/{sex}")
    public Result submitOrder(@ApiParam(name = "scheduleId", value = "排期ID", required = true)
                                  @PathVariable String scheduleId,
                              @ApiParam(name = "name", value = "联系人", required = true) @PathVariable String name,
                              @ApiParam(name = "phone", value = "联系邮箱", required = true) @PathVariable String phone,
                              @ApiParam(name = "sex", value = "性别", required = true) @PathVariable Integer sex,
                              HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        Long orderId = orderService.saveOrder(scheduleId, userId, name, phone, sex);
        return Result.ok(orderId);
    }

    //根据订单id查询订单详情
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    /**
     * 条件获取用户订单
     * @param orderCountQueryVo
     * @return
     */
    @GetMapping("auth/getUserOrders")
    public Result getUserOrders(@ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false)
                                            OrderCountQueryVo orderCountQueryVo,
                                HttpServletRequest httpServletRequest) {
        List<OrderInfo> userOrders = null;
        try {
            orderCountQueryVo.setUserId(AuthContextHolder.getUserId(httpServletRequest));
            userOrders = orderService.getUserOrders(orderCountQueryVo);
        } catch (Exception e) {
            log.error("getUserOrders error: ", e);
        }
        return Result.ok(userOrders);
    }

    //取消预约
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {
        Boolean isOrder = false;
        try {
            isOrder = orderService.cancelOrder(orderId);
        } catch (Exception e) {
            if (e instanceof HospitalException) {
                return Result.fail(isOrder);
            }
         log.error("auth/cancelOrder error");
        }

        return Result.ok(isOrder);
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }


}
