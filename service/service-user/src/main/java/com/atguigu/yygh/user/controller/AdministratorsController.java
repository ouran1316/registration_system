package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.user.service.AdministratorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/11 10:21
 * 管理员处理controller
 */
@RestController
@RequestMapping("/admin/administrators")
public class AdministratorsController {

    @Autowired
    private AdministratorsService administratorsService;

    /**
     * 管理员系统登陆
     * @param userName
     * @param password
     * @return
     */
    @GetMapping("administratorsLogin/{userName}/{password}")
    public Result administratorsLogin(@PathVariable String userName, @PathVariable String password) {
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

}
