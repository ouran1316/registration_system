package com.atguigu.yygh.hosp.controller;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 20:13
 */

@Api(tags = "单位设置管理")
@RestController
@RequestMapping(value = "/admin/hosp/hospitalSet")
//@CrossOrigin    //允许跨域请求
public class HospitalSetController {

    //注入 service
    @Autowired
    HospitalSetService hospitalSetService;

    //1 查询单位设置所有信息
    @ApiOperation(value = "获取所有单位设置")
    @GetMapping("/findAll")
    public Result findAllHospitalSet() {
        //调用 service
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 逻辑删除单位设置
    @ApiOperation(value = "逻辑删除单位设置")
    @DeleteMapping("/{id}")
    public Result removeHospSet(@PathVariable("id") Long id) {
        boolean b = hospitalSetService.removeById(id);
        return Result.ok();
    }

    //3 条件查询带分页
    /**
     * @param current 页码
     * @param limit 每页大小
     * @param hospitalSetQueryVo 单位名称和单位编号
     * @return 分页结果
     */
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable("current") long current,
                                  @PathVariable("limit") long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {

        Page<HospitalSet> page = new Page<>(current, limit);

        //调用方法实现分页查询
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, new QueryWrapper<HospitalSet>()
                .like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()), "hosname", hospitalSetQueryVo.getHosname())
                .eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()), "hoscode", hospitalSetQueryVo.getHoscode())
        );

        //返回结果
        return Result.ok(hospitalSetPage);
    }

    //4 添加单位设置
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态 1 使用 0 不使用
        hospitalSet.setStatus(1);
        //签名密钥
        hospitalSet.setSignKey(
                SecureUtil.md5(System.currentTimeMillis() + "" + new Random().nextInt(1000)));
        //调用 service 保存
        if (hospitalSetService.save(hospitalSet)) {
            return Result.ok();
        }
        return Result.fail();
    }

    //5 根据 id 获取单位配置
    @GetMapping("/getHospSet/{id}")
    public Result getHospSetById(@PathVariable("id") long id) {
//        try{
//            int a = 1 / 0;
//        } catch (Exception e) {
//            throw new HospitalException("错误", 302);
//        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //6 修改单位设置
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //省略好多对 hospitalSet 安全性判断
        if (hospitalSetService.updateById(hospitalSet)) {
            return Result.ok();
        }
        return Result.fail();
    }

    //7 批量删除单位设置
    @DeleteMapping("/batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> ids) {
        //这里是逻辑删除
        hospitalSetService.removeByIds(ids);
        return Result.ok();
    }

    //8 单位设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {
        //根据id查询单位设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //9 发送签名秘钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }

}
