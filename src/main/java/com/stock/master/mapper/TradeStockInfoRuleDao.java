package com.stock.master.mapper;

import com.stock.master.model.po.TradeStockInfoRule;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;

public interface TradeStockInfoRuleDao {

    PageVo<TradeStockInfoRule> get(PageParam pageParam);

    void updateStateById(int state, int id);

}
