package com.atguigu.yygh.user.service.Impl;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.util.SlidingWindowCounter;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 14:53
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private PatientService patientService;

    @Autowired
    private SlidingWindowCounter slidingWindowCounter;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final static int windowInSecond = 60;
    private final static int maxCount = 5;

    //用户手机号登陆接口，正式登陆方法
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {

        //从loginVo获取输入的手机号，和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断手机号和验证码是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
        }

        //判断手机验证码和输入的验证码是否一致
        //注：redis 里存的是 邮箱验证码 本次直接使用页面验证码吧，不用手机验证码了
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(redisCode)) {
            //TODO 假如登陆限流
            //限流处理
            if (!slidingWindowCounter.canAccess(phone, windowInSecond, maxCount)) {
                throw new HospitalException(ResultCodeEnum.LOGIN_LIMIT);
            }
            throw new HospitalException(ResultCodeEnum.CODE_ERROR);
        }

        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
//            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
//            if(null != userInfo) {
//                userInfo.setPhone(loginVo.getPhone());
//                this.updateById(userInfo);
//            } else {
//                throw new HospitalException(ResultCodeEnum.DATA_ERROR);
//            }
        }

        //如果userinfo为空，进行正常手机登录
        if(userInfo == null) {
            //判断是否第一次登录：根据手机号查询数据库，如果不存在相同手机号就是第一次登录
            userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>()
                    .eq("phone", phone));
            if (userInfo == null) { //第一次使用这个手机号登陆
                //添加信息到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new HospitalException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //不是第一次，直接登录
        //返回登录信息
        //返回登录用户名
        //返回token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //jwt token 生成
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        return map;
    }

//    //用户手机号登陆接口2，用于测试限流算法的效果
//    @Override
//    public Map<String, Object> loginUser(LoginVo loginVo) {
//
//        //从loginVo获取输入的手机号，和验证码
//        String phone = loginVo.getPhone();
//        String code = loginVo.getCode();
//
//        //判断手机号和验证码是否为空
//        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
//            throw new HospitalException(ResultCodeEnum.PARAM_ERROR);
//        }
//
//        if (slidingWindowCounter.overMaxCount(phone, windowInSecond, maxCount)) {
//            throw new HospitalException(ResultCodeEnum.LOGIN_LIMIT);    //333
//        }
//
//        UserInfo userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>()
//                .eq("phone", phone)
//                .eq("openid", Integer.parseInt(code))
//        );
//
//        if(userInfo == null) {
//            //限流处理
////            if (!slidingWindowCounter.canAccess(phone, windowInSecond, maxCount)) {
////                throw new HospitalException(ResultCodeEnum.LOGIN_LIMIT);    //333
////            }
//            throw new HospitalException(ResultCodeEnum.CODE_ERROR); //210
//        }
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", userInfo.getName());
//        return map;
//    }

    //用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        baseMapper.updateById(userInfo);
    }

    //获取就诊人列表
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        Page<UserInfo> pages = baseMapper.selectPage(pageParam, new QueryWrapper<UserInfo>()
                .like(!StringUtils.isEmpty(name), "name", name)
                .eq(!StringUtils.isEmpty(status), "status", status)
                .eq(!StringUtils.isEmpty(authStatus), "auth_status", authStatus)
                .ge(!StringUtils.isEmpty(createTimeBegin), "create_time", createTimeBegin)
                .le(!StringUtils.isEmpty(createTimeEnd), "create_time", createTimeEnd)
        );
        pages.getRecords().stream().forEach((item) -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    //用户详情
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //用户信息
        UserInfo userInfo = packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo", userInfo);

        //就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList", patientList);
        return map;
    }

    //认证审批
    @Override
    public void approval(Long userId, Integer authStatus) {
        //2 是认证成功 -1 是认证失败
        if (authStatus == 2 || authStatus == -1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号变成对应值封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证对应值封装
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        String statusString = userInfo.getStatus().intValue()==0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);
        return userInfo;
    }

}
