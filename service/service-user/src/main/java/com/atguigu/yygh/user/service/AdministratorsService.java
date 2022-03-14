package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.AdministratorsInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/11 9:58
 * 管理员服务
 */
public interface AdministratorsService extends IService<AdministratorsInfo> {
    /**
     * 管理员登陆，请求manger系统
     * @param userName
     * @param password
     */
    Boolean login(String userName, String password);
}
