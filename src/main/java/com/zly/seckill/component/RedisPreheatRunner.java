package com.zly.seckill.component;

import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.SeckillActivity;
import com.zly.seckill.services.RedisService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisPreheatRunner  implements ApplicationRunner {
    @Resource
    RedisService redisService;
    @Resource
    SeckillActivityDao seckillActivityDao;

    public RedisPreheatRunner() {
        System.out.println(">>> RedisPreheatRunner constructor invoked");
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> RedisPreheatRunner PostConstruct");
    }

    /**
     * 启动项目时 向 Redis 存入 商品库存
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<SeckillActivity> seckillActivities =
                seckillActivityDao.querySeckillActivitysByStatus(1);
        System.out.println("Fetched activities: " + seckillActivities.size());
        // set all activityId to the key of redis
        for (SeckillActivity seckillActivity : seckillActivities) {
            System.out.println(seckillActivity.getId());
            redisService.setValue("stock:" + seckillActivity.getId(), (long) seckillActivity.getAvailableStock());
        }
    }
}
