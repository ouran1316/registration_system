package com.atguigu.hospital.util;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.hospital.mapper.HospitalSetMapper;
import com.atguigu.hospital.mapper.UserMapper;
import com.atguigu.hospital.model.HospitalSet;
import com.atguigu.hospital.model.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 17:55
 */
@Slf4j
public class CommonHolder {

   private final static ThreadLocal<Map<String, Object>> commonHolder = new ThreadLocal<Map<String, Object>>(){
       @Override
       protected Map<String, Object> initialValue() {
           return Maps.newHashMap();
       }
   };

    /**
     * 根据管理员账号查询到hoscode，并存到holder中
     * @param request
     * @param redisTemplate
     */
   public static void setHoscodeByUserName(HttpServletRequest request, RedisTemplate<String, String> redisTemplate) {
       String user_name = (String) request.getSession().getAttribute("user_name");
       if (StringUtils.isBlank(user_name)) {
           throw new YyghException(ResultCodeEnum.USER_NOT_LOGGER);
       }
       // 1.先走缓存
       String bufferHosCode = redisTemplate.opsForValue().get(CommonConstant.preFix_hosCode + user_name);
       if (!StringUtils.isBlank(bufferHosCode)) {
           CommonHolder.setHoscode(bufferHosCode);
           CommonHolder.setUserName(user_name);
       }
       // 2.查库
       Map<String, Object> paramsMap = Maps.newHashMap();
       JSONObject response = null;
       try {
           paramsMap.put("user_name", user_name);
           response = HttpRequestHelper.sendRequest(
                   paramsMap, CommonConstant.apiUrl + "/admin/administrators/getAdminHoscode");
       } catch (Exception e) {
           log.error("com/atguigu/hospital/util/CommonHolder.java setHoscodeByUserName error:", e);
       }
       if (response == null || !ResultCodeEnum.SUCCESS.getCode().equals(response.getIntValue("code"))) {
           throw new YyghException(ResultCodeEnum.USER_NOT_LOGGER);
       }
       String hoscode = response.getString("data");

       if (!StringUtils.isBlank(hoscode)) {
           CommonHolder.setHoscode(hoscode);
           CommonHolder.setUserName(user_name);
           // 存入缓存
           redisTemplate.opsForValue()
                   .set(CommonConstant.preFix_hosCode + user_name, hoscode, 24, TimeUnit.HOURS);
       } else {
           throw new YyghException(ResultCodeEnum.USER_NOT_LOGGER);
       }
   }

   public static void setHoscode(String hoscode) {
       Map<String, Object> map = commonHolder.get();
       map.put("hoscode", hoscode);
   }

   public static String getHoscode() {
       Map<String, Object> map = commonHolder.get();
       Object hoscode = map.get("hoscode");
       if (hoscode != null) {
           return (String) hoscode;
       }
       return null;
   }

    public static void setUserName(String userName) {
        Map<String, Object> map = commonHolder.get();
        map.put("userName", userName);
    }

    public static String getUserName() {
        Map<String, Object> map = commonHolder.get();
        Object userName = map.get("userName");
        if (userName != null) {
            return (String) userName;
        }
        return null;
    }

    /**
     * 使用完必须要清除掉，防止内存泄露
     */
    public static void clean() {
       commonHolder.remove();
    }
}
