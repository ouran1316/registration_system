package com.atguigu.hospital.service.impl;

import com.atguigu.hospital.mapper.HospitalSetMapper;
import com.atguigu.hospital.model.HospitalSet;
import com.atguigu.hospital.service.HospitalSetService;
import com.atguigu.hospital.util.CommonHolder;
import com.atguigu.hospital.util.ResultCodeEnum;
import com.atguigu.hospital.util.YyghException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/9 11:53
 */
@Service
@Slf4j
public class HospitalSetServiceImpl implements HospitalSetService {

    @Resource
    HospitalSetMapper hospitalSetMapper;

    @Override
    public HospitalSet getHospitalSet() {
        HospitalSet hospitalSet = null;
        try {
            String hoscode = CommonHolder.getHoscode();
            hospitalSet = hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>()
                    .eq("hoscode", hoscode));
        } catch (Exception e) {
            log.error("getHospitalSet error:", e);
        }
        return hospitalSet == null ? new HospitalSet() : hospitalSet;
    }

    @Override
    public void updateHospitalSet(HospitalSet hospitalSet) {
        String hoscode = CommonHolder.getHoscode();
        if (StringUtils.isBlank(hoscode) || !hoscode.equals(hospitalSet.getHoscode())) {
            throw new YyghException(ResultCodeEnum.USER_NOT_AUTHENTICATE);
        }
        try {
            HospitalSet hs = hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>().eq("hoscode", hoscode));
            if (hs == null) {
                hospitalSetMapper.insert(hospitalSet);
            } else {
                hospitalSetMapper.update(hospitalSet, new QueryWrapper<HospitalSet>().eq("hoscode", hoscode));
            }
        } catch (Exception e) {
            log.error("updateHospitalSet error:", e);
        }
    }

}
