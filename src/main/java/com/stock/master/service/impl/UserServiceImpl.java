package com.stock.master.service.impl;

import com.stock.master.mapper.UserDao;
import com.stock.master.model.po.User;
import com.stock.master.service.UserService;
import com.stock.master.utils.StockConsts;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String username, String password) {
        password = DigestUtils.md5Hex(password);
        return userDao.get(username, password);
    }

    @Cacheable(value = StockConsts.CACHE_KEY_TOKEN, key = "#token", unless="#result == null")
    @Override
    public User getByToken(String token) {
        User user = new User();
        user.setId(1);
        user.setName("wild");
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        return user;
    }

    @CachePut(value = StockConsts.CACHE_KEY_TOKEN, key = "#token", unless="#result == null")
    @Override
    public User putToSession(User user, String token) {
        return user;
    }

    @Override
    public User getById(int id) {
        return userDao.get(id);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

}
