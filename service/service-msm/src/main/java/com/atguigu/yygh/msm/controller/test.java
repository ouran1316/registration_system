package com.atguigu.yygh.msm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 17:50
 */
@RestController
public class test {
    @GetMapping("/test")
    public void test() {
        System.out.println("21313132131321312321321");
        System.out.println("commit1");
        System.out.println("commit2");
    }
}
