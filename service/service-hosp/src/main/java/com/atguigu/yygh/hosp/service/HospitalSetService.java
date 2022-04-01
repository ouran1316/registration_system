package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 20:06
 */
public interface HospitalSetService extends IService<HospitalSet> {
    //根据传递过来的单位编码，查询数据库，查询签名
    String getSignKey(String hoscode);

    //获取单位签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
