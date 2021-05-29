package com.stock.master.service;


import com.stock.master.api.response.GetDealDataResponse;
import com.stock.master.api.response.GetHisDealDataResponse;
import com.stock.master.api.response.GetOrdersDataResponse;
import com.stock.master.api.response.GetStockListResponse;
import com.stock.master.model.po.TradeMethod;
import com.stock.master.model.po.TradeOrder;
import com.stock.master.model.po.TradeRule;
import com.stock.master.model.po.TradeUser;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.model.vo.trade.DealVo;
import com.stock.master.model.vo.trade.OrderVo;
import com.stock.master.model.vo.trade.StockVo;
import com.stock.master.model.vo.trade.TradeConfigVo;

import java.util.List;

public interface TradeService {

    TradeMethod getTradeMethodByName(String name);

    TradeUser getTradeById(int id);

    void update(TradeUser tradeUser);

    TradeRule getTradeRuleByStockCode(String stockCode);

    PageVo<TradeRule> getRuleList(PageParam pageParam);

    PageVo<TradeConfigVo> getConfigList(PageParam pageParam);

    void changeConfigState(int state, int id);

    List<TradeOrder> getTradeOrderList();

    List<TradeOrder> getTodayTradeOrderList();

    void saveTradeOrder(TradeOrder tradeOrder);

    List<DealVo> getTradeDealList(List<GetDealDataResponse> data);

    void deleteTradeCode(String tradeCode, String tradeType);

    List<StockVo> getTradeStockList(List<GetStockListResponse> stockList);

    List<OrderVo> getTradeOrderList(List<GetOrdersDataResponse> orderList);

    List<DealVo> getTradeHisDealList(List<GetHisDealDataResponse> data);

}
