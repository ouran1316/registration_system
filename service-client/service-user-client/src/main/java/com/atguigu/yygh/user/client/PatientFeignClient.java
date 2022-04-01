package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/30 10:29
 */
@FeignClient("service-user")
@Repository
public interface PatientFeignClient {
    /**
     * 获取就诊人
     * @param id 就诊人id
     * @return 就诊人详细信息
     */
    @GetMapping("/api/user/patient/inner/get/{id}")
    Patient getPatientOrder(@PathVariable("id") Long id);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/admin/user/getUserInfo")
    UserInfo getUserInfo(Long userId);
}
