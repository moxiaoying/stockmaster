package com.stock.master.service.impl;

import com.stock.master.mapper.RobotDao;
import com.stock.master.model.po.Robot;
import com.stock.master.service.RobotService;
import com.stock.master.utils.StockConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RobotServiceImpl implements RobotService {

    private static final String ID_SYSTEM = "1";

    @Autowired
    private RobotDao robotDao;

    @Cacheable(value = StockConsts.CACHE_KEY_CONFIG_ROBOT, key = RobotServiceImpl.ID_SYSTEM)
    @Override
    public Robot getSystem() {
        return getById(Integer.parseInt(RobotServiceImpl.ID_SYSTEM));
    }

    @Cacheable(value = StockConsts.CACHE_KEY_CONFIG_ROBOT, key = "#id")
    @Override
    public Robot getById(int id) {
        return robotDao.getById(id);
    }

}
