package com.atguigu.hospital.config;

import com.atguigu.hospital.mapper.UserMapper;
import com.atguigu.hospital.util.CommonHolder;
import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/9 11:23
 * 用户权限鉴定
 */
@Component
@Aspect
public class AuthenticateAspect {

    // 排除内部请求接口
    private static final List<String> constantCheck = Lists.newArrayList();

    static {
        constantCheck.add("getSignKey");
        constantCheck.add("getApiUrl");
        constantCheck.add("submitOrder");
        constantCheck.add("updatePayStatus");
        constantCheck.add("updateCancelStatus");
        constantCheck.add("userLogin");
    }

    @Autowired
    HttpServletRequest request;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Pointcut("execution(* com.atguigu.hospital.service.impl..*.*(..))")
    public void pointcut() {
    }

//    @Before("pointcut()")
//    public void authenticatePreCheck(JoinPoint joinPoint) {
//        String methodName = joinPoint.getSignature().getName();
//        if (!constantCheck.contains(methodName)) {
//            // 初始化hoscode、user_name
//            CommonHolder.setHoscodeByUserName(request, userMapper);
//        }
//    }

    @Around("pointcut()")
    public Object authenticateProCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        if (!constantCheck.contains(methodName)) {
            // 初始化hoscode、user_name
            CommonHolder.setHoscodeByUserName(request, redisTemplate);
        }

        Object obj = joinPoint.proceed();

        CommonHolder.clean();
        return obj;
    }
}
