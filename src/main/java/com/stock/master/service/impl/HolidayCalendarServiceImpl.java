package com.stock.master.service.impl;

import com.stock.master.mapper.HolidayCalendarDao;
import com.stock.master.model.po.HolidayCalendar;
import com.stock.master.service.HolidayCalendarService;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HolidayCalendarServiceImpl implements HolidayCalendarService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HolidayCalendarDao holidayCalendarDao;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void updateCurrentYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Map<?, ?> data = restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=" + year, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Integer> dateInfo = (Map<String, Integer>) data.get(String.valueOf(year));
        List<HolidayCalendar> list = dateInfo.entrySet().stream().filter(entry -> entry.getValue() != 0).map(entry -> {
            Date date = DateUtils.parseDate(year + entry.getKey(), new String[] { "yyyyMMdd" });
            HolidayCalendar holidayCalendar = new HolidayCalendar();
            holidayCalendar.setDate(date);
            return holidayCalendar;
        }).collect(Collectors.toList());

        holidayCalendarDao.deleteByYear(year);
        holidayCalendarDao.save(list);
    }

    @Override
    public boolean isHoliday(Date date) {
        return holidayCalendarDao.getByDate(date) != null;
    }

}
