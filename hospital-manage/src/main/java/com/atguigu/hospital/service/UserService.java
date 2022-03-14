package com.atguigu.hospital.service;

import com.atguigu.hospital.model.UserInfo;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 11:09
 */
public interface UserService {
    /**
     * 管理员登陆
      * @param userName
     * @param password
     * @return
     */
    public Boolean userLogin(String userName, String password);
}
