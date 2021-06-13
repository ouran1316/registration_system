package com.atguigu.yygh.oss.service.Impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.common.exception.HospitalException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/28 16:55
 */
@Service
public class FileServiceImpl implements FileService {

    //上传文件到阿里云oss
    @Override
    public String upload(MultipartFile file) {

        try {
            // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
            String endpoint = ConstantOssPropertiesUtils.EDNPOINT;
            String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
            String accessKeySecret = ConstantOssPropertiesUtils.SECRECT;
            String bucketName = ConstantOssPropertiesUtils.BUCKET;

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传文件流。
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            //生成随机唯一值，使用uuid 按日期创建文件夹
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = new DateTime().toString("yyyy/MM/dd") + "/" + uuid + fileName;



            //1.jpg  /a/b/1.jpg
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();

            //上传后文件路径
            //https://yygh-atguigu-ouran.oss-cn-guangzhou.aliyuncs.com/dict.xlsx
            return "https://" + bucketName + "." + endpoint + "/" + fileName;

        } catch (IOException e) {
            throw new HospitalException(ResultCodeEnum.SERVICE_ERROR);
        }
    }
}
