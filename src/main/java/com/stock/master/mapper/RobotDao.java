package com.stock.master.mapper;

import com.stock.master.model.po.Robot;

import java.util.List;

public interface RobotDao {

    Robot getById(int id);

    List<Robot> getListByType(int type);

}
