package com.stock.master.service;

import com.stock.master.model.po.DailyIndex;
import com.stock.master.model.po.StockInfo;
import com.stock.master.utils.StockConsts;


import java.util.List;

public interface StockCrawlerService {

    List<StockInfo> getStockList();

    StockConsts.StockState getStockState(String code);

    DailyIndex getDailyIndex(String code);

    List<DailyIndex> getHistoryDailyIndexs(String code);

    String getHistoryDailyIndexsString(String code);

}
