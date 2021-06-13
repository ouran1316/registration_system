package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 14:53
 */
public interface UserInfoService extends IService<UserInfo> {
    //用户手机号登陆接口
    Map<String, Object> loginUser(LoginVo loginVo);

    //用户认证
    void userAuth(Long userId, UserAuthVo userAuthVo);

    //获取就诊人、用户条件查询、待审批用户列表
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    //用户详情
    Map<String, Object> show(Long userId);

    //认证审批
    void approval(Long userId, Integer authStatus);
}
