package com.stock.master.mapper;


import com.stock.master.model.po.StockInfo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;

import java.util.List;

public interface StockInfoDao {

    void add(List<StockInfo> list);

    void update(List<StockInfo> list);

    PageVo<StockInfo> get(PageParam pageParam);

    StockInfo getStockByFullCode(String code);

}
