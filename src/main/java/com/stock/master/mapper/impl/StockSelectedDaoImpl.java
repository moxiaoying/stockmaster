package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.StockSelectedDao;
import com.stock.master.model.po.StockSelected;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockSelectedDaoImpl extends BaseDao implements StockSelectedDao {

    private static final String SQL_SELECT_BASE_COLUMNS = "select id as id, code as code, rate as rate, create_time as createTime, update_time as updateTime, description as description from stock_selected where 1 = 1";

    @Override
    public List<StockSelected> getList() {
        List<StockSelected> list = jdbcTemplate.query(SQL_SELECT_BASE_COLUMNS,
                new Object[] {}, BeanPropertyRowMapper.newInstance(StockSelected.class));
        return list;
    }

}
