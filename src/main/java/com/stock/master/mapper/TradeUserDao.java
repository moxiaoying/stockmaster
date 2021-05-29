package com.stock.master.mapper;

import com.stock.master.model.po.TradeUser;

public interface TradeUserDao {

    TradeUser getById(int id);

    void update(TradeUser tradeUser);

}
