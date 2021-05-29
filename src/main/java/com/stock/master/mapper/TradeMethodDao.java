package com.stock.master.mapper;


import com.stock.master.model.po.TradeMethod;

public interface TradeMethodDao {

    TradeMethod getByName(String name);

}
