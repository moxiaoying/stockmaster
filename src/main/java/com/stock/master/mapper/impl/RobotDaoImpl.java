package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.RobotDao;
import com.stock.master.model.po.Robot;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class RobotDaoImpl extends BaseDao implements RobotDao {

    private static final String SELECT_SQL = "select id, type, webhook, state from robot where 1 = 1";

    @Override
    public Robot getById(int id) {
        String sql = RobotDaoImpl.SELECT_SQL + " and id = ?";
        return jdbcTemplate.queryForObject(sql, new Integer[] { id }, BeanPropertyRowMapper.newInstance(Robot.class));
    }

    @Override
    public List<Robot> getListByType(int type) {
        String sql = RobotDaoImpl.SELECT_SQL + " and type = ?";
        return jdbcTemplate.query(sql, new Integer[] { type }, BeanPropertyRowMapper.newInstance(Robot.class));
    }

}
