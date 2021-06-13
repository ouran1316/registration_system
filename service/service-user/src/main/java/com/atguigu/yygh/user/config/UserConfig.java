package com.atguigu.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 15:02
 */
@Configuration
@MapperScan("com.atguigu.yygh.user.mapper")
public class UserConfig {
}
