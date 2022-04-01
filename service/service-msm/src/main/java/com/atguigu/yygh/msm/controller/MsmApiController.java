package com.atguigu.yygh.msm.controller;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 17:18
 */
@Slf4j
@Api
@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送邮箱验证码
    @ApiOperation(value = "sendemail")
    @GetMapping("/send")
    public Result sendCode(@Param("phone") String phone) {
        //从redis获取验证码，如果获取获取到，返回ok
        // key 手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果从redis获取不到，
        // 生成验证码，
        code = RandomUtil.getSixBitRandom();
        //调用service方法，通过整合短信服务进行发送
        Boolean isSend = false;
        try {
            isSend = msmService.send(phone, code);
        } catch (Exception e) {
            log.error("MsmApiController sendCode msmService.send error" + e);
        }

        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            redisTemplate.opsForValue().set(phone, code,2, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送邮箱验证码失败");
        }
    }

}
