package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 18:35
 */
@SpringBootApplication
//因为 swagger 配置文件在service-util，配置注解扫描包才能扫到
@ComponentScan(basePackages = "com.atguigu")
@EnableDiscoveryClient
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
