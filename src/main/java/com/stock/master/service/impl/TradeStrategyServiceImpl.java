package com.stock.master.service.impl;

import com.stock.master.mapper.TradeStrategyDao;
import com.stock.master.model.po.TradeStrategy;
import com.stock.master.service.TradeStrategyService;
import com.stock.master.utils.StockConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeStrategyServiceImpl implements TradeStrategyService {

    @Autowired
    private TradeStrategyDao tradeStrategyDao;

    @Cacheable(value = StockConsts.CACHE_KEY_TRADE_STRATEGY)
    @Override
    public List<TradeStrategy> getAll() {
        return tradeStrategyDao.getAll();
    }

}
