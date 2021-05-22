package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理模块
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/16 16:17
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    //自定义异常处理方法
    @ExceptionHandler(HospitalException.class)
    public Result error(HospitalException e) {
        return Result.build(e.getCode(), e.getMessage());
    }


}
