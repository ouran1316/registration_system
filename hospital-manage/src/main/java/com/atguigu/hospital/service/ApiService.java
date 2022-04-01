package com.atguigu.hospital.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

public interface ApiService {

    String getSignKey();

    String getApiUrl();

    JSONObject getHospital();

    boolean saveHospital(String data);

    Map<String, Object> findDepartment(int pageNum, int pageSize);

    boolean saveDepartment(String data);

    boolean removeDepartment(String depcode);

    Map<String, Object> findSchedule(int pageNum, int pageSize);

    boolean saveSchedule(String data);

    /**
     * 更新单个排期
     * @param scheduleId
     * @param reversedNumber
     * @param amount
     * @param skill
     * @return
     */
    Boolean updateSchedule(String scheduleId, String reversedNumber, String amount, String skill);

    boolean removeSchedule(String hosScheduleId);

    void  saveBatchHospital() throws IOException;
}
