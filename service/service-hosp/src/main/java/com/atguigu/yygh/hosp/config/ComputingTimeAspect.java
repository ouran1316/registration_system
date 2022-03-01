package com.atguigu.yygh.hosp.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/2/15 22:37
 * 计算方法消耗时间切面
 */
@Component
@Aspect
public class ComputingTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(ComputingTimeAspect.class);

    @Pointcut("execution(* com.atguigu.yygh.hosp.service.impl..*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object computingTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                String methodName = ((MethodSignature) joinPoint.getSignature()).getName();
                saveTimeLog(methodName, obj.toString(), startTime);
            } catch (Exception e) {
            }
        }
        return obj;
    }

    /**
     * 记录方法调用时间
     * @param methodName
     * @param startTime
     */
    private void saveTimeLog(String methodName, String content, long startTime) {
        long diffTime = System.currentTimeMillis() - startTime;
        logger.info("method:[" + methodName + "] " + "costTime:" + diffTime + "ms;" + "content: " + content);
    }

}
