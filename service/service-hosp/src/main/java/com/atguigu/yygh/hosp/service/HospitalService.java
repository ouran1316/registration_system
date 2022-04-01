package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/21 11:16
 */
public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    //单位列表(条件查询分页)
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新单位上线状态
    void updateStatus(String id, Integer status);

    //单位详情信息
    Map<String, Object> getHospById(String id);

    //获取单位名称
    String getHospName(String hoscode);

    //根据单位名称模糊查询
    List<Hospital> findByHosname(String hosname);

    //根据单位编号获取单位预约挂号详情
    Map<String, Object> item(String hoscode);
}
