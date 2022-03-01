package com.atguigu.yygh.common.helper;

import com.alibaba.excel.util.StringUtils;
import io.jsonwebtoken.*;

import java.util.Date;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/25 15:35
 */
public class JwtHelper {

    //过期时间
    private static long tokenExpiration = 24*60*60*1000;
    //签名密钥
    private static String tokenSignKey = "123456";

    //根据参数生成 token
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    //根据 token 得到用户 id
    //如果 token 已经过期这里还获取会报500
    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Integer userId;
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            userId = (Integer)claims.get("userId");
        } catch (Exception e) {
            return null;
        }
        return userId.longValue();
    }

    //根据 token 获取 username
    //如果 token 已经过期这里还获取会报500
    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }

    //判断 token 是否已经过期


    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "ouran");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
