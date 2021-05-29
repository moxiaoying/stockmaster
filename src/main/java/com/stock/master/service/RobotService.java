package com.stock.master.service;

import com.stock.master.model.po.Robot;

public interface RobotService {

    Robot getSystem();

    Robot getById(int id);

}
