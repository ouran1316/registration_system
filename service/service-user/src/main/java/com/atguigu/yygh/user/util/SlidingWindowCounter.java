package com.atguigu.yygh.user.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/9/8 11:26
 * redis使用zset实现滑动窗口计数
 */
@Component
@Slf4j
public class SlidingWindowCounter {
    /**
     * Redis key 前缀
     */
    private static final String SLIDING_WINDOW = "sliding_window_";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 判断 key 的 value 中有效访问次数是否超过最大限定值 maxCount
     * 判断与数量增长分开处理
     * @param key   redis key
     * @param windowInSecond    窗口间隔，秒
     * @param maxCount  最大计数
     * @return 是 or 否
     */
    public boolean overMaxCount(String key, int windowInSecond, long maxCount) {
        key = SLIDING_WINDOW + key;
        log.info("redis key = {}", key);
        //当前时间
        long currentMs = System.currentTimeMillis();
        //窗口开始时间
        long windowStartMs = currentMs - windowInSecond * 1000L;
        //按 score 统计 key 的 value 中有效数量
        Long count = redisTemplate.opsForZSet().count(key, windowStartMs, currentMs);
        //以访问次数 >= 最大可访问值
        return count >= maxCount;
    }

    /**
     * 判断 key 的 value 中的有效访问次数是否超过最大限定值 maxCount，若没超过，调用 increment 方法，将窗口内的访问数加一
     * 判断与数量增长同步处理
     * @param key   redis key
     * @param windowInSecond    窗口间隔，秒
     * @param maxCount  最大计数
     * @return 可访问 or 不可访问
     */
    public boolean canAccess(String key, int windowInSecond, long maxCount) {
        key = SLIDING_WINDOW + key;
        log.info("redis key = {}", key);
        //按 key 统计集合中的有效数量
        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count < maxCount) {
            increment(key, windowInSecond);
            return true;
        } else {
            return false;
        }
    }

    /**
     * h滑动窗口技术增长
     * @param key   redis key
     * @param windowInSecond    窗口间隔
     */
    public void increment(String key, Integer windowInSecond) {
        //当前时间
        long currentMs = System.currentTimeMillis();
        //窗口开始时间
        long windowStartMs = currentMs - windowInSecond * 1000;
        //单例模式，为提升性能
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        //清除窗口过期成员
        zSetOperations.removeRangeByScore(key, 0, windowStartMs);
        //唯一value
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //添加当前时间 value = uuid score = 当前时间戳
        zSetOperations.add(key, uuid, currentMs);
        //设置 key 过期时间
        redisTemplate.expire(key, windowInSecond, TimeUnit.SECONDS);

    }

}
