package com.oss.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/28 16:25
 */
@PropertySource({"classpath:application.properties"})
public class OssTest {

    @Value("${aliyun.oss.accessKeyId}")
    private static String accessKeyId = "LTAI5t9U7bGAxULGP7VsiSLa";

    @Value("${aliyun.oss.accessKeySecret}")
    private static String accessKeySecret = "jiejf1Ao1EmbcbmetI7v257p2CaGNC";

    public static void main(String[] args) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
//        String accessKeyId = "<yourAccessKeyId>";
//        String accessKeySecret = "<yourAccessKeySecret>";
        String bucketName = "yygh-testoss233333";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建存储空间。
        ossClient.createBucket(bucketName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
