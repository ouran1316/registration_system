package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 17:10
 */
public interface MsmService {
    //发送邮箱验证码
    boolean send(String email, String code);

    //mq 使用发送邮箱
    boolean send(MsmVo msmVo);

    //发送大量预约提醒
    boolean sends(MsmVo[] msmVos);
}
