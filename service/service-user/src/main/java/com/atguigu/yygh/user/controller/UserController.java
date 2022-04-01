package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.model.user.UserInfoVo;
import com.atguigu.yygh.user.service.AdministratorsService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/29 10:51
 */
@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    //用户列表（田间查询带分页）
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    //用户详情
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }

    //获取用户 id 信息接口，内部接口
    @GetMapping("getUserInfo")
    public Result getUserInfo(Long userId) {
        UserInfoVo userInfoVo = new UserInfoVo();
        try {
            UserInfo userInfo = userInfoService.getById(userId);
            userInfo.setPassword(null);
            BeanUtils.copyProperties(userInfo, userInfoVo);
        } catch (Exception e) {
            log.error("auth/getUserInfo error", e);
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        }
        return Result.ok(userInfoVo);
    }

}
