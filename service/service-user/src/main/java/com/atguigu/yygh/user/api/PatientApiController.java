package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/29 8:47
 */
@Api("就诊人管理")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController  {

    @Autowired
    PatientService patientService;

    //获取就诊人列表
    @GetMapping("/auth/findAll")
    public Result findAll(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }

    //添加就诊人（不一定是用户本人,可以userId下绑定了多个就诊人）
    @PostMapping("/auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        //TODO 这里添加一个身份证编号是否存在的判断，已存在直接报错

        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //根据id 获取就诊人信息
    @GetMapping("/auth/get/{id}")
    public Result getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }

    //修改就诊人
    @PostMapping("/auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    //删除就诊人
    @DeleteMapping("/auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取就诊人")
    @GetMapping("/inner/get/{id}")
    public Patient getPatientOrder(@ApiParam(name = "id", value = "就诊人id", required = true)
                                       @PathVariable("id") Long id) {
        return patientService.getById(id);
    }


}
