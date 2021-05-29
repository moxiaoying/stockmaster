package com.stock.master.service.impl;

import com.stock.master.mapper.StockSelectedDao;
import com.stock.master.model.po.StockSelected;
import com.stock.master.service.StockSelectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class StockSelectedServiceImpl implements StockSelectedService {

    @Autowired
    private StockSelectedDao stockSelectedDao;

    @Override
    public List<StockSelected> getList() {
        return stockSelectedDao.getList();
    }

}
