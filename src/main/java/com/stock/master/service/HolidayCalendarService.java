package com.stock.master.service;

import java.util.Date;

public interface HolidayCalendarService {

    void updateCurrentYear();

    boolean isHoliday(Date date);

}
