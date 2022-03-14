package com.atguigu.hospital.service;

import com.atguigu.hospital.model.HospitalSet;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/9 11:51
 * 单位设置服务
 */
public interface HospitalSetService {

    /**
     * 获取单位设置信息
     * @return
     */
    public HospitalSet getHospitalSet();

    /**
     * 更新单位设置信息
     */
    public void updateHospitalSet(HospitalSet hospitalSet);
}
