package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/28 16:55
 */
public interface FileService {
    //上传文件到阿里云oss
    String upload(MultipartFile file);
}
