package com.zly.seckill.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    /**
     * The reason we usually make JedisPool final in constructor injection is to show that:
     * It should never change once it’s set by Spring (which happens when the object is created).
     * It helps prevent accidental reassignment in the code.
     * It makes the class easier to reason about and ensures immutability for injected dependencies.
    */
    private final JedisPool jedisPool;

    public RedisService(JedisPool jedis) {
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
     * 设置值
     *
     * @param key
     * @param value
     */
    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
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

    /**
     * Overdue payment Redis Inventory rollback
     *
     * @param key
     */
    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
        jedisClient.close();
    }

    /**
     * Determine whether the user is on the purchase restriction list
     * @param seckillActivityId
     * @param userId
     * @return
     */
    public boolean isInLimitMember(long seckillActivityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
        log.info("userId:{} activityId:{} is on the purchase restriction list {}", userId, seckillActivityId, sismember);
        return sismember;
    }

    /**
     * add user to a specific purchase restriction list
     * @param seckillActivityId
     * @param userId
     */
    public void addLimitMember(long seckillActivityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
    }

    /**
     * Remove user from the purchase restriction list
     * @param seckillActivityId
     * @param userId
     */
    public void removeLimitMember(Long seckillActivityId, Long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
    }

    /**
     * Obtain the distributed lock
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @return
     */
    public  boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedisClient = jedisPool.getResource();


        /*
          nxxx 参数有两个值可选 ：
            NX： not exists, 只有key 不存在时才把 key value set  到 redis
            XX： is exists ，只有 key 存在是，才把 key value set  到 redis
          expx 参数有两个值可选 ：
            EX：seconds 秒
            PX: milliseconds 毫秒
         */
        // old method
        // String result = jedisClient.set(lockKey, requestId, "NX", "PX", expireTime);

        SetParams params = new SetParams().nx().px(expireTime);
        String result = jedisClient.set(lockKey, requestId, params);
        jedisClient.close();
        // return false means that locking is failed
        return "OK".equals(result);
    }

    /**
     * Release the distributed lock
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedisClient = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = (Long) jedisClient.eval(script,
                Collections.singletonList(lockKey), Collections.singletonList(requestId));
        jedisClient.close();
        if (result == 1L) {
            return true;
        }
        return false;
    }
}
