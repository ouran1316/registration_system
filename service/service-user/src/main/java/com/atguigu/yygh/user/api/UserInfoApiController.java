package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.model.user.UserInfoVo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 14:52
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserInfoApiController {

    @Autowired
    UserInfoService userInfoService;

    //用户手机号登陆、注册接口
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        if (loginVo == null || StringUtils.isBlank(loginVo.getType())) {
            return Result.build(loginVo, ResultCodeEnum.PARAM_ERROR);
        }

        Map<String, Object> info = null;
        try {
            if ("login".equals(loginVo.getType())) {
                info = userInfoService.loginUser(loginVo);
            } else if ("register".equals(loginVo.getType())) {
                info = userInfoService.registerUser(loginVo);
            } else {
                return Result.build(loginVo, ResultCodeEnum.DATA_ERROR);
            }
        } catch (Exception e) {
            if (e instanceof HospitalException) {
                HospitalException exception = (HospitalException) e;
                return Result.build(exception.getCode(), exception.getMessage());
            }
        }
        return Result.ok(info);
    }

    /**
     * 保存用户信息
     * @param userInfoVo 用户基本信息
     * @param code 验证码
     * @return 保存结果
     */
    @PostMapping("/auth/saveUserInfo/{code}")
    public Result saveUserInfo(@RequestBody UserInfoVo userInfoVo, @PathVariable String code,
                               HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        Map<String, Object> result = null;
        try {
            result = userInfoService.saveUserInfo(userInfoVo, code, userId);
        } catch (Exception e) {
            if (e instanceof HospitalException) {
                HospitalException exception = (HospitalException) e;
                return Result.build(exception.getCode(), exception.getMessage());
            }
        }
        return Result.ok(result);
    }

    //用户认证接口
    @PostMapping("/auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();
    }

    //获取用户 id 信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        UserInfoVo userInfoVo = new UserInfoVo();
        try {
            Long userId = AuthContextHolder.getUserId(request);
            UserInfo userInfo = userInfoService.getById(userId);
            userInfo.setPassword(null);
            BeanUtils.copyProperties(userInfo, userInfoVo);
        } catch (Exception e) {
            log.error("auth/getUserInfo error", e);
            return Result.build(null, ResultCodeEnum.SERVICE_ERROR);
        }

        return Result.ok(userInfoVo);
    }

    //获取用户列表
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, @PathVariable UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoQueryVo);
        return Result.ok(pageModel);
    }
}
