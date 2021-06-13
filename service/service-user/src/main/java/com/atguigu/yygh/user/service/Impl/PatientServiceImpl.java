package com.atguigu.yygh.user.service.Impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/29 8:48
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    //获取就诊人列表
    @Override
    public List<Patient> findAllUserId(Long userId) {
        List<Patient> patientList = baseMapper.selectList(new QueryWrapper<Patient>()
                .eq("user_id", userId)
        );
        //通过远程调用，得到编码对应具体内容，查询字典表
        patientList.stream().forEach(item -> {
            this.packPatient(item);
        });
        return patientList;
    }

    //根据id 获取就诊人信息
    @Override
    public Patient getPatientId(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packPatient(patient);
        return patient;
    }

    //Patient都对象里面其它参数封装
    private void packPatient(Patient patient) {
        String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        //联系人（家属）证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
    }


}
