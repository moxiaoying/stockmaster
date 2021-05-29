package com.stock.master.service;



import com.stock.master.model.po.DailyIndex;
import com.stock.master.model.po.StockInfo;
import com.stock.master.model.po.StockLog;
import com.stock.master.model.vo.DailyIndexVo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;

import java.util.Date;
import java.util.List;

public interface StockService {

    List<StockInfo> getAll();

    List<StockInfo> getAllListed();

    void addStockLog(List<StockLog> list);

    void update(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList);

    void saveDailyIndexToFile(String rootPath);

    void saveDailyIndexFromFile(String rootPath);

    void saveDailyIndex(DailyIndex dailyIndex);

    void saveDailyIndex(List<DailyIndex> list);

    PageVo<StockInfo> getStockList(PageParam pageParam);

    StockInfo getStockByFullCode(String code);

    PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam);

    List<DailyIndex> getDailyIndexListByDate(Date date);

}
