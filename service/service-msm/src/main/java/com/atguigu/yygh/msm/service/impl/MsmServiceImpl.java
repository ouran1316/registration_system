package com.atguigu.yygh.msm.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 17:11
 */
@Service
public class
MsmServiceImpl implements MsmService {

    @Autowired
    JavaMailSenderImpl mailSender;

    //发送邮箱验证码
    @Override
    public boolean send(String email, String code) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(email)) {
            return false;
        }

        //邮件设置1：一个简单的邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("尚医通预约挂号平台通知");
        message.setText(code);

        message.setTo(email);
        message.setFrom("1316049625@qq.com");
        mailSender.send(message);

        //这里应该要加一个异常捕获

        return true;
    }

    //mq 使用发送邮箱
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())) {
            return this.send(msmVo.getPhone(), msmVo.getParam());
        }
        return false;
    }

    private boolean send(String email, Map<String, Object> param) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(email)) {
            return false;
        }

        //邮件设置1：一个简单的邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject((String) param.get("title"));
        message.setText(JSONObject.toJSONString((String) param.get("reserveDate")));

        message.setTo(email);
        message.setFrom("1316049625@qq.com");
        try{
            mailSender.send(message);
        } catch (MailSendException me) {
            System.out.println("发送邮件时发生异常！可能有无效的邮箱");
        }


        //这里应该要加一个异常捕获

        return true;
    }
}
