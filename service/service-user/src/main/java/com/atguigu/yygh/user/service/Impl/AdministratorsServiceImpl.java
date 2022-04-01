package com.atguigu.yygh.user.service.Impl;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.model.user.AdministratorsInfo;
import com.atguigu.yygh.user.mapper.AdministratorsMapper;
import com.atguigu.yygh.user.service.AdministratorsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/11 9:59
 */
@Service
@Slf4j
public class AdministratorsServiceImpl extends ServiceImpl<AdministratorsMapper, AdministratorsInfo> implements AdministratorsService {
    @Override
    public Boolean login(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new HospitalException(ResultCodeEnum.LOGIN_MOBLE_ERROR);
        }
        AdministratorsInfo administratorsInfo;
        try {
            administratorsInfo =
                    baseMapper.selectOne(new QueryWrapper<AdministratorsInfo>().eq("user_name", userName));
        } catch (Exception e) {
            log.error("AdministratorsServiceImpl login error", e);
            throw new HospitalException(ResultCodeEnum.SERVICE_ERROR);
        }
        if (administratorsInfo == null || !administratorsInfo.getPassword().equals(MD5.encrypt(password))) {
            throw new HospitalException(ResultCodeEnum.LOGIN_MOBLE_ERROR);
        }
        return true;
    }

    @Override
    public String getHoscodeByAdminName(String userName) {
        if (StringUtils.isEmpty(userName)) {
            throw new HospitalException(ResultCodeEnum.LOGIN_AUTH);
        }
        AdministratorsInfo administratorsInfo = null;
        try {
            administratorsInfo =
                    baseMapper.selectOne(new QueryWrapper<AdministratorsInfo>().eq("user_name", userName));
        } catch (Exception e) {
            log.error("AdministratorsServiceImpl login error", e);
            throw new HospitalException(ResultCodeEnum.SERVICE_ERROR);
        }
        if (administratorsInfo == null || administratorsInfo.getHoscode() == null) {
            return null;
        }
        return administratorsInfo.getHoscode();
    }
}
