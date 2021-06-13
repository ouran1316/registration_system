package com.atguigu.yygh.common.utils;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/28 20:27
 */

//获取当前用户信息工具类
public class AuthContextHolder {

    //获取当前用户id
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);

        return userId;
    }

    //获取当前用户名称
    public static String getUserName(HttpServletRequest request) {
        return JwtHelper.getUserName(request.getHeader("token"));
    }
}
