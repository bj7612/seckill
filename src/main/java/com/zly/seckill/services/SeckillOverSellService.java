package com.zly.seckill.services;

import com.zly.seckill.db.dao.SeckillActivityDao;
import com.zly.seckill.db.po.SeckillActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillOverSellService {

    @Autowired
    SeckillActivityDao seckillActivityDao;

    public String processSeckill(long activityId) {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(activityId);
        int availableStock = activity.getAvailableStock();
        String result;

        if (availableStock > 0) {
            result = "Congratulation, seckill success";
            System.out.println(result);
            availableStock-= 1;
            activity.setAvailableStock(availableStock);
            seckillActivityDao.updateSeckillActivity(activity);
        } else {
            result = "Sorry, seckill failed, stock is over";
            System.out.println(result);
        }

        return result;
    }

}
