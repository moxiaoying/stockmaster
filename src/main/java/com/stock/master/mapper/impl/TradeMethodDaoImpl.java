package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.TradeMethodDao;
import com.stock.master.model.po.TradeMethod;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TradeMethodDaoImpl extends BaseDao implements TradeMethodDao {

    @Override
    public TradeMethod getByName(String name) {
        return jdbcTemplate.queryForObject("select id, name, url, state from trade_method where name = ?",
                new String[] { name }, BeanPropertyRowMapper.newInstance(TradeMethod.class));
    }

}
