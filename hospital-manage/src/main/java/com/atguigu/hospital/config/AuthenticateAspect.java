package com.atguigu.hospital.config;

import com.atguigu.hospital.mapper.UserMapper;
import com.atguigu.hospital.util.CommonHolder;
import com.atguigu.hospital.util.YyghException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/9 11:23
 * 用户权限鉴定
 */
@Component
@Aspect
public class AuthenticateAspect {

    @Autowired
    HttpServletRequest request;

    @Resource
    UserMapper userMapper;

    @Pointcut("execution(* com.atguigu.hospital.service.impl..*.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void authenticatePreCheck(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        // 排除登陆接口
        if (!methodName.equals("userLogin")) {
            // 初始化hoscode、user_name
            CommonHolder.setHoscodeByUserName(request, userMapper);
        }

    }

}
