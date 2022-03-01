package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.HospFeighClient;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.mapper.OrderMapper;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 9:59
 */
@Service
public class OrderSereviceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospFeighClient hospFeighClient;

    @Autowired
    private RabbitService rabbitService;

    //生成挂号订单
    @Override
    public Long saveOrder(String scheduleId, Long userId, String name, String phone, Integer sex) {
        //获取就诊人信息
        // Patient patient = patientFeignClient.getPatientOrder(patientId);

        //获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospFeighClient.getScheduleOrderVo(scheduleId);

        //判断当前时间是否还可以预约
        // todo 这里时间的判断规则需要修改，目前是没有对时间判断，什么时候都可以下单

//        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
//                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
//            throw new HospitalException(ResultCodeEnum.TIME_NO);
//        }

        //获取签名信息
        SignInfoVo signInfoVo = hospFeighClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        //将 scheduleOrderVo 数据复制到 orderInfo
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);

        //向orderInfo设置其他数据
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
        orderInfo.setUserId(userId);
//        orderInfo.setPatientId(patientId);
//        orderInfo.setPatientName(patient.getName());
//        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setName(name);
        orderInfo.setPhone(phone);
        orderInfo.setSex(sex);
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口，实现预约挂号操作
        //设置调用医院接口需要参数，参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());

        paramMap.put("name", name);
//        paramMap.put("certificatesType",patient.getCertificatesType());
//        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", sex);
//        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", phone);
//        paramMap.put("isMarry", patient.getIsMarry());
//        paramMap.put("provinceCode",patient.getProvinceCode());
//        paramMap.put("cityCode", patient.getCityCode());
//        paramMap.put("districtCode",patient.getDistrictCode());
//        paramMap.put("address",patient.getAddress());
//        //联系人
//        paramMap.put("contactsName",patient.getContactsName());
//        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
//        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
//        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        //请求医院系统接口
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        if(result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq消息，号源更新和短信通知
            //发送mq信息更新号源
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //订单id
            orderMqVo.setId(orderInfo.getId());
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午" : "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);

            //发送到 mq，更新 mongodb 预约数 和发邮件
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            //发送到 mq，发送邮件提醒
//            if(null != msmVo) {
//                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, msmVo);
//            }

        } else {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }

    // 根据订单id查询订单详情
    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // 补充场地号和预约时间段
        Result<Schedule> result = hospFeighClient.getSchedule(orderInfo.getScheduleId());
        Schedule schedule = result.getData();
        orderInfo.getParam().put("docname", schedule.getDocname());
        orderInfo.getParam().put("skill", schedule.getSkill());
        return this.packOrderInfo(orderInfo);
    }

    @Override
    public List<OrderInfo> getUserOrders(OrderCountQueryVo orderCountQueryVo) {
        if (null == orderCountQueryVo.getUserId()) {
            throw new HospitalException(ResultCodeEnum.USERID_ERROR);
        }
        if (orderCountQueryVo.getReserveDateBegin().isEmpty()) {
            orderCountQueryVo.setReserveDateBegin("2000-01-01");
        }
//        if (orderCountQueryVo.getReserveDateEnd().isEmpty()) {
////            orderCountQueryVo.setReserveDateEnd(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + 7);
////        }
        List<OrderInfo> orderInfos = baseMapper.selectUserOrders(orderCountQueryVo);
        // 补充场地号和预约时间段
        for (OrderInfo orderInfo : orderInfos) {
            Result<Schedule> result = hospFeighClient.getSchedule(orderInfo.getScheduleId());
            Schedule schedule = result.getData();
            orderInfo.getParam().put("docname", schedule.getDocname());
            orderInfo.getParam().put("workDate",
                    new SimpleDateFormat("yyyy-MM-dd")
                            .format(schedule.getWorkDate()) + " " + schedule.getSkill());
            this.packOrderInfo(orderInfo);
        }
        return orderInfos;
    }


    //取消预约
    @Override
    public Boolean cancelOrder(Long orderId) {
        // 获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // 判断是否超过了可取消预约时间
        Schedule schedule = hospFeighClient.getSchedule(orderInfo.getScheduleId()).getData();
        String[] date = new SimpleDateFormat("yyyy-MM-dd#HH：mm").format(new Date()).split("#");
        String areaValidTime = schedule.getSkill().split("-")[0].trim();
        if (areaValidTime.split("：")[0].length() < 2) {
            areaValidTime = "0" + areaValidTime;
        }
        if (schedule.getWorkDate().toString().compareTo(date[0]) < 0
                || (schedule.getWorkDate().toString().compareTo(date[0]) == 0
                && areaValidTime.compareTo(date[1]) < 0)) {
            throw new HospitalException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        // 已支付退款逻辑 因为没有支付系统，跳过
//        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
//        if (quitTime.isBeforeNow()) {
//            throw new HospitalException(ResultCodeEnum.CANCEL_ORDER_NO);
//        }

        //调用医院接口实现预约取消
        //根据医院接口返回数据，判断是否做退款操作
        SignInfoVo signInfoVo = hospFeighClient.getSignInfoVo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("scheduleId", orderInfo.getScheduleId());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        JSONObject result = HttpRequestHelper.sendRequest(reqMap,
                signInfoVo.getApiUrl()+"/order/updateCancelStatus");

        //根据医院接口返回数据
        if(result.getInteger("code") != 200) {
            throw new HospitalException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            // 判断当前订单是否可以取消
            // 支付退款逻辑跳过
            if(orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()
                    || orderInfo.getOrderStatus().intValue() == OrderStatusEnum.UNPAID.getStatus().intValue()) {
                //TODO 微信退款

                //更新订单状态
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);

                //发送mq更新预约数量
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                orderMqVo.setReservedNumber(1);
                orderMqVo.setAvailableNumber(1);
                //短信提示
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
                String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle() + "取消预约");
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                }};
                msmVo.setParam(param);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            }
        }
        return true;
    }

    //就诊通知
    @Override
    public void patientTips() {
        List<OrderInfo> orderInfoList = baseMapper.selectList(new QueryWrapper<OrderInfo>()
                .eq("reserve_date", new DateTime().toString("yyyy-MM-dd"))
                .ne("order_status", OrderStatusEnum.CANCLE.getStatus() ));

        MsmVo[] msmVos = new MsmVo[orderInfoList.size()];
        int x = 0;
        for(OrderInfo orderInfo:orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            msmVos[x++] = msmVo;
//            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
        //发过去一个集合
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVos);
    }

    //预约统计
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //调用 mapper 方法得到数据，获取哪一天预约有多少人
        List<OrderCountVo> orderCountVos = baseMapper.selectOrderCount(orderCountQueryVo);

        //获取 x 需要的数据，日期数据，list 集合
        List<String> dateList = orderCountVos.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());

        //获取 y 轴需要的数据，具体数量， list 集合
        List<Integer> countList = orderCountVos.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

        HashMap<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
