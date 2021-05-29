package com.stock.master.mapper;

import com.stock.master.model.po.TradeOrder;

import java.util.Date;
import java.util.List;


public interface TradeOrderDao {

    void save(TradeOrder tradeOrder);

    List<TradeOrder> getAll();

    List<TradeOrder> getListByDate(Date date);

    void delete(String tradeCode, String tradeType);

}