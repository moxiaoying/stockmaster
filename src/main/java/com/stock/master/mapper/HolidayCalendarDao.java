package com.stock.master.mapper;


import com.stock.master.model.po.HolidayCalendar;

import java.util.Date;
import java.util.List;

public interface HolidayCalendarDao {

    void deleteByYear(int year);

    void save(List<HolidayCalendar> list);

    HolidayCalendar getByDate(Date date);

}
