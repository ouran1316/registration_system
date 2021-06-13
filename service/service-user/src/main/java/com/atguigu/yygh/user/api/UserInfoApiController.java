package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class UserInfoApiController {

    @Autowired
    UserInfoService userInfoService;

    //用户手机号登陆接口
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
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
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }

    //获取用户列表
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit, @PathVariable UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoQueryVo);
        return Result.ok(pageModel);
    }
}
