package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.UserDao;
import com.stock.master.model.po.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

    private static final String SQL_SELECT = "select id, username, password, name, mobile, email, create_time as createTime, update_time as updateTime from user where 1 = 1";

    @Override
    public User get(String username, String password) {
        List<User> list = jdbcTemplate.query(
                UserDaoImpl.SQL_SELECT + " and username = ? and password = ?",
                new String[] { username, password }, BeanPropertyRowMapper.newInstance(User.class));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public User get(int id) {
        List<User> list = jdbcTemplate.query(
                UserDaoImpl.SQL_SELECT + " and id = ?",
                new Integer[] { id }, BeanPropertyRowMapper.newInstance(User.class));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("update user set password = ? where id = ?", user.getPassword(), user.getId());
    }

}
