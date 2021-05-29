package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.TradeStrategyDao;
import com.stock.master.model.po.TradeStrategy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TradeStrategyDaoImpl extends BaseDao implements TradeStrategyDao {

    private static final String SQL_SELECT_BASE_COLUMNS = "select id as id, name as name, bean_name as beanName, state as state, create_time as createTime, update_time as updateTime from trade_strategy where 1 = 1";

    @Override
    public List<TradeStrategy> getAll() {
        return jdbcTemplate.query(SQL_SELECT_BASE_COLUMNS, BeanPropertyRowMapper.newInstance(TradeStrategy.class));
    }

}
