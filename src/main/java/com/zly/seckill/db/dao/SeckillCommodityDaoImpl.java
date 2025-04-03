package com.zly.seckill.db.dao;

import com.zly.seckill.db.mappers.SeckillCommodityMapper;
import com.zly.seckill.db.po.SeckillCommodity;
import com.zly.seckill.db.dao.SeckillCommodityDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SeckillCommodityDaoImpl implements SeckillCommodityDao {

    @Resource
    private SeckillCommodityMapper seckillCommodityMapper;

    @Override
    public SeckillCommodity querySeckillCommodityById(long commodityId) {
        return seckillCommodityMapper.selectByPrimaryKey(commodityId);
    }
}
