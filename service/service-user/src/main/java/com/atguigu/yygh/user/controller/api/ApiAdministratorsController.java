package com.atguigu.yygh.user.controller.api;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.user.service.AdministratorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/11 10:35
 * 管理员api服务
 */
@RestController
@RequestMapping("/admin/administrators")
public class ApiAdministratorsController {

    @Autowired
    AdministratorsService administratorsService;

    /**
     * 管理员系统登陆
     * @param request
     * @return
     */
    @PostMapping("administratorsLogin")
    public Result administratorsLogin(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramsMap = HttpRequestHelper.switchMap(parameterMap);
        String userName = (String) paramsMap.get("user_name");
        String password = (String) paramsMap.get("password");
        try {
            administratorsService.login(userName, password);
        } catch (Exception e) {
            if (e instanceof HospitalException) {
                HospitalException exception = (HospitalException) e;
                return Result.build(exception.getCode(), exception.getMessage());
            }
        }
        return Result.ok(userName);
    }

    /**
     * 获取管理员Hoscode
     * @param request
     * @return
     */
    @PostMapping("getAdminHoscode")
    public Result getAdminHoscode(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramsMap = HttpRequestHelper.switchMap(parameterMap);
        String userName = (String) paramsMap.get("user_name");
        String hoscode;
        try {
            hoscode = administratorsService.getHoscodeByAdminName(userName);
        } catch (Exception e) {
            return Result.fail();
        }
        return Result.ok(hoscode);
    }
}
