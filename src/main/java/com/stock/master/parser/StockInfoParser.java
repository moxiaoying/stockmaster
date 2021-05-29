package com.stock.master.parser;


import com.stock.master.model.po.StockInfo;
import com.stock.master.utils.StockConsts.StockState;

import java.util.List;

public interface StockInfoParser {

    List<StockInfo> parseStockInfoList(String content);

    StockState parseStockState(String content);

}
