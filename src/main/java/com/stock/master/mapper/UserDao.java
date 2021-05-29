package com.stock.master.mapper;

import com.stock.master.model.po.User;

public interface UserDao {

    User get(String username, String password);

    User get(int id);

    void update(User user);

}
