package com.stock.master.mapper.impl;

import java.util.Date;
import java.util.List;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.TradeOrderDao;
import com.stock.master.model.po.TradeOrder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TradeOrderDaoImpl extends BaseDao implements TradeOrderDao {

    private static final String SELECT_SQL = "select id, trade_code as tradeCode, stock_code as stockCode, price, volume, trade_type as tradeType, entrust_code as entrustCode, create_time as createTime, update_time as updateTime from trade_order where 1 = 1";

    @Override
    public void save(TradeOrder tradeOrder) {
        jdbcTemplate.update(
                "insert into trade_order(trade_code, stock_code, price, volume, trade_type, entrust_code) values(?, ?, ?, ?, ?, ?)",
                tradeOrder.getTradeCode(), tradeOrder.getStockCode(), tradeOrder.getPrice(),
                tradeOrder.getVolume(), tradeOrder.getTradeType(), tradeOrder.getEntrustCode());
    }

    @Override
    public List<TradeOrder> getAll() {
        return jdbcTemplate.query(TradeOrderDaoImpl.SELECT_SQL, new Object[] {},
                BeanPropertyRowMapper.newInstance(TradeOrder.class));
    }

    @Override
    public List<TradeOrder> getListByDate(Date date) {
        return jdbcTemplate.query(TradeOrderDaoImpl.SELECT_SQL + " and create_time >= ?",
                new Object[] { new java.sql.Date(date.getTime()) },
                BeanPropertyRowMapper.newInstance(TradeOrder.class));
    }

    @Override
    public void delete(String tradeCode, String tradeType) {
        jdbcTemplate.update("delete from trade_order where trade_code = ? and trade_type = ?", tradeCode, tradeType);
    }


}