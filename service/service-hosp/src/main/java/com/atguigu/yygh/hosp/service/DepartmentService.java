package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 14:51
 */
public interface DepartmentService {
    //上传场地接口
    void save(Map<String, Object> paramMap);

    //查询场地接口
    Page<Department> findPageDepartment(int page, int limit, Department department);

    //删除场地接口
    void remove(String hoscode, String depcode);

    //根据单位编号，查询单位所有场地列表
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据单位编号和场地编号查询场地名称
    String getDepName(String hoscode, String depcode);

    //根据单位编号和场地编号查询场地
    Department getDepartment(String hoscode, String depcode);
}
