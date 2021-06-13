package com.atguigu.yygh.oss.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/28 16:45
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    FileService fileService;

    //上传文件到阿里云oss
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) {
        //获取上传文件
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
