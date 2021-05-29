package com.stock.master.mapper;


import com.stock.master.model.po.TradeRule;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;

public interface TradeRuleDao {

    TradeRule getTradeRuleByStockCode(String stockCode);

    PageVo<TradeRule> get(PageParam pageParam);

}
