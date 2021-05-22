package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/22 14:52
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    //上传科室接口
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
