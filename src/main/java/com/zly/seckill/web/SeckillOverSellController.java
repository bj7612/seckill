package com.zly.seckill.web;

import com.zly.seckill.services.SeckillActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class SeckillOverSellController {

    //@Resource
    //SeckillOverSellService seckillOverSellService;
    @Resource
    SeckillActivityService seckillActivityService;

    /**
    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckill(@PathVariable long seckillActivityId) {
        return seckillOverSellService.processSeckill(seckillActivityId);
    }
    */

    /**
     * 使用 lua 脚本处理抢购请求
     * @param seckillActivityId activity id
     * @return String
     */
    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckillCommodity(@PathVariable long seckillActivityId) {
        boolean stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
        return stockValidateResult ? "恭喜你秒杀成功" : "商品已经售完，下次再来";
    }
}
