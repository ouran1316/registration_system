package com.atguigu.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 10:09
 */
@Configuration
@MapperScan("com.atguigu.yygh.order.mapper")
public class OrderConfig {
}
