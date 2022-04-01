package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;

import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 20:07
 */

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    //根据传递过来的单位编码，查询数据库，查询签名
    @Override
    public String getSignKey(String hoscode) {
        return baseMapper.selectOne(new QueryWrapper<HospitalSet>()
                .eq("hoscode", hoscode)).getSignKey();
    }

    //获取单位签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        HospitalSet hospitalSet = baseMapper.selectOne(new QueryWrapper<HospitalSet>()
                .eq("hoscode", hoscode));
        if (hospitalSet == null) throw new HospitalException(ResultCodeEnum.HOSPITAL_OPEN);

        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }
}
