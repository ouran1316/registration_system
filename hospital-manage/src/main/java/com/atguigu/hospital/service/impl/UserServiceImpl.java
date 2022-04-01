package com.atguigu.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.hospital.mapper.UserMapper;
import com.atguigu.hospital.model.UserInfo;
import com.atguigu.hospital.service.UserService;
import com.atguigu.hospital.util.*;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 11:11
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public Boolean userLogin(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            throw new YyghException(ResultCodeEnum.USER_LOGIN_ERROR);
        }
        Map<String, Object> paramsMap = Maps.newHashMap();
        JSONObject respone;
        try {
            paramsMap.put("user_name", userName);
            paramsMap.put("password", password);
            respone = HttpRequestHelper.sendRequest(
                    paramsMap, CommonConstant.apiUrl + "/admin/administrators/administratorsLogin");
        } catch (Exception e) {
            log.error("UserServiceImpl userLogin selectByMap error", e);
            return false;
        }
        if (null == respone) {
            throw new YyghException(ResultCodeEnum.SERVICE_ERROR);
        }
        if (!ResultCodeEnum.SUCCESS.getCode().equals(respone.getIntValue("code"))) {
            throw new YyghException(respone.getString("message"), respone.getIntValue("code"));
        }
        return true;
    }
}
