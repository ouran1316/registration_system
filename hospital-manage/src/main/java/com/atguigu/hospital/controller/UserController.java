package com.atguigu.hospital.controller;

import com.atguigu.hospital.model.UserInfoVo;
import com.atguigu.hospital.service.UserService;
import com.atguigu.hospital.util.ImageUtil;
import com.atguigu.hospital.util.Result;
import com.atguigu.hospital.util.ResultCodeEnum;
import com.atguigu.hospital.util.YyghException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2022/3/8 9:29
 */
@Slf4j
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login")
    @ResponseBody
    @ApiOperation(value = "管理员登陆")
    public Result userLogin(@RequestBody UserInfoVo userInfoVo,
                            HttpServletRequest request) {
        try {
            String password = userInfoVo.getPassword();
            userInfoVo.setPassword(null);
            // 验证码校验
            String img_verify_code = (String) request.getSession().getAttribute("img_verify_code");
            Long startTime = (Long) request.getSession().getAttribute("img_verify_time");
            if (img_verify_code.isEmpty() || null == startTime) {
                return Result.build(userInfoVo, ResultCodeEnum.DATA_ERROR);
            }
            request.getSession().removeAttribute("img_verify_code");
            request.getSession().removeAttribute("img_verify_time");

            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 5 * 60 * 1000 || img_verify_code.isEmpty()) {
                return Result.build(userInfoVo, ResultCodeEnum.VALIDATE_CODE_TIMEOUT);
            }
            if (!img_verify_code.equals(userInfoVo.getCode())) {
                return Result.build(userInfoVo, ResultCodeEnum.VALIDATE_CODE_ERROR);
            }

            // 登陆校验
            Boolean result = userService.userLogin(userInfoVo.getUserName(), password);
            if (!result) {
                return Result.build(userInfoVo, ResultCodeEnum.SERVICE_ERROR);
            }

        } catch (Exception e) {
            if (e instanceof YyghException) {
                YyghException exception = (YyghException) e;
                return Result.build(userInfoVo, ResultCodeEnum.getResultCodeEnum(exception.getCode()));
            }
            return Result.build(userInfoVo, ResultCodeEnum.DATA_ERROR);
        }
        // session 记录登陆信息
        request.getSession().setAttribute("user_name", userInfoVo.getUserName());
        return Result.ok(userInfoVo);
    }

    @RequestMapping(value = "/logout")
    @ApiOperation(value = "管理员登出")
    public String userLogout(ModelMap modelMap, HttpServletRequest request) {
        request.getSession().removeAttribute("user_name");
        return "/frame/login";
    }

    @RequestMapping(value = "/validate/code")
    @ResponseBody
    @ApiOperation("generate image verification code")
    public void generateImgVerificationCode(HttpServletRequest request, HttpServletResponse response) {

        try {
            int width = 129;
            int height = 40;
            BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 生成对应宽高的初始图片
            // 生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            String randomText = ImageUtil.drawRandomText(width, height, verifyImg);

            request.getSession().setAttribute("img_verify_code", randomText.toUpperCase());
            request.getSession().setAttribute("img_verify_time", System.currentTimeMillis());
            // 设置session过期时间为5分钟
            request.getSession().setMaxInactiveInterval(5 * 60);

            response.setContentType("image/png"); // 必须设置响应内容类型为图片，否则前台不识别
            OutputStream os = response.getOutputStream(); // 获取文件输出流
            ImageIO.write(verifyImg, "png", os); // 输出图片流

            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("generateImgVerificationCode error", e);
        }
    }
}
