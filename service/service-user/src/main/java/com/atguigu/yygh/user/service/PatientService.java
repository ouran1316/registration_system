package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/29 8:48
 */
public interface PatientService extends IService<Patient> {

    //获取就诊人列表
    List<Patient> findAllUserId(Long userId);

    //根据id 获取就诊人信息
    Patient getPatientId(Long id);
}
