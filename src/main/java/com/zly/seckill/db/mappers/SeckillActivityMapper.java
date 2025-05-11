package com.zly.seckill.db.mappers;

import com.zly.seckill.db.po.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeckillActivityMapper {
    int deleteByPrimaryKey(Long id);

    void insert(SeckillActivity record);

    int insertSelective(SeckillActivity record);

    SeckillActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillActivity record);

    int updateByPrimaryKey(SeckillActivity record);

    List<SeckillActivity> querySeckillActivitysByStatus(int activityStatus);

    int lockStock(Long seckillActivityId);

    int deductStock(Long seckillActivityId);
}