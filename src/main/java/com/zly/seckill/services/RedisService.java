package com.zly.seckill.services;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Service
public class RedisService {
    /**
     * The reason we usually make JedisPool final in constructor injection is to show that:
     * It should never change once it’s set by Spring (which happens when the object is created).
     * It helps prevent accidental reassignment in the code.
     * It makes the class easier to reason about and ensures immutability for injected dependencies.
    */
    private final JedisPool jedisPool;

    RedisService(JedisPool jedis) {
        this.jedisPool = jedis;
    }

    /**
     * Set key, value
     * @param key
     * @param value
     */
    public void setValue(String key, Long value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value.toString());
        jedisClient.close();
    }
    /**
     * get value
     * @param key
     * @return
     */
    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }

    /**
     * 缓存中库存判断和扣减
     * @param key
     * @return
     * @throws Exception
     */
    public boolean stockDeductValidator(String key) {
        try(Jedis jedisClient = jedisPool.getResource()) {
            String script = "if redis.call('exists',KEYS[1]) == 1 then\n" +
                    "   local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "   if( stock <=0 ) then\n" +
                    "       return -1\n" +
                    "   end;\n" +
                    "   redis.call('decr',KEYS[1]);\n" +
                    "   return stock - 1;\n" +
                    " end;\n" +
                    " return -1;";
            Long stock = (Long) jedisClient.eval(script, Collections.singletonList(key), Collections.emptyList());

            if (stock < 0) {
                System.out.println("库存不足");
                return false;
            } else {
                System.out.println("恭喜，抢购成功");
            }
            return true;
        } catch (Throwable throwable) {
            System.out.println("库存扣减失败：" + throwable.toString());
            return false;
        }
    }
}
