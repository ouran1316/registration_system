package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 14:51
 */
public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(int page, int limit, Department department);

    //删除科室接口
    void remove(String hoscode, String depcode);
}
