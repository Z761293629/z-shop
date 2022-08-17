package com.zhaoyg.lock;

import com.zhaoyg.CouponServiceBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;

/**
 * @author zhao
 * @date 2022/8/15
 */
class RedisLuaTest extends CouponServiceBootTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void test() {
        String luaScript = "if (redis.call('EXISTS',KEYS[1]) == 0) " +
                "then " +
                "   redis.call('SET',KEYS[1],ARGV[1]);" +
                "   redis.call('EXPIRE',KEYS[1],ARGV[2]);" +
                "   return nil;" +
                "   " +
                "else " +
                "   return nil;" +
                "end";
        redisTemplate.execute(new DefaultRedisScript<>(luaScript), Arrays.asList("test"), "abcd", 1000);
    }
}
