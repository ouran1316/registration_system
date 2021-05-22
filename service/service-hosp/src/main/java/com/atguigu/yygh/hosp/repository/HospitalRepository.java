package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/21 11:14
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    //判断是否存在数据，按mongo标准方法名
    Hospital getHospitalByHoscode(String hoscode);
}
