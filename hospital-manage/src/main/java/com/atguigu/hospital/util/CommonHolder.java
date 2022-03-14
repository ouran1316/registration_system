package com.atguigu.hospital.util;

import com.atguigu.hospital.mapper.HospitalSetMapper;
import com.atguigu.hospital.mapper.UserMapper;
import com.atguigu.hospital.model.HospitalSet;
import com.atguigu.hospital.model.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 17:55
 */
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
     * @param userMapper
     */
   public static void setHoscodeByUserName(HttpServletRequest request, UserMapper userMapper) {
       String user_name = (String) request.getSession().getAttribute("user_name");
        if (StringUtils.isBlank(user_name)) {
            throw new YyghException(ResultCodeEnum.USER_NOT_LOGGER);
        }
       UserInfo userInfo = userMapper.selectOne(new QueryWrapper<UserInfo>()
               .eq("user_name", user_name));
       CommonHolder.setHoscode(userInfo.getHoscode());
       CommonHolder.setUserName(user_name);
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
}
