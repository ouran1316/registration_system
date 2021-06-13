package com.atguigu.yygh.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 9:53
 */
@SpringBootApplication
@EnableDiscoveryClient
//需要包扫描通常是因为要调用的是别的模块下的方法
@ComponentScan(basePackages = {"com.atguigu"})
@EnableFeignClients(basePackages = {"com.atguigu"})
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
