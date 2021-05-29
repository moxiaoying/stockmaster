package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.TradeUserDao;
import com.stock.master.model.po.TradeUser;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TradeUserDaoImpl extends BaseDao implements TradeUserDao {

    @Override
    public TradeUser getById(int id) {
        return jdbcTemplate.queryForObject(
                "select id, name, account_id as accountId, cookie, validate_key as validateKey from trade_user where id = ?",
                new Integer[] { id }, BeanPropertyRowMapper.newInstance(TradeUser.class));
    }

    @Override
    public void update(TradeUser tradeUser) {
        jdbcTemplate.update("update trade_user set cookie = ?, validate_key = ? where id = ?",
                tradeUser.getCookie(), tradeUser.getValidateKey(), tradeUser.getId());
    }

}
