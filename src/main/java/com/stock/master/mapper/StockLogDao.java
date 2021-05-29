package com.stock.master.mapper;


import com.stock.master.model.po.StockLog;

import java.util.List;

public interface StockLogDao {

    void add(List<StockLog> list);

    void setStockIdByCodeType(List<String> list, int type);

}
