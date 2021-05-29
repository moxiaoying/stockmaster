package com.stock.master.mapper;



import com.stock.master.model.po.DailyIndex;
import com.stock.master.model.vo.DailyIndexVo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;

import java.util.Date;
import java.util.List;

public interface DailyIndexDao {

    int save(DailyIndex dailyIndex);

    void save(List<DailyIndex> list);

    PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam);

    List<DailyIndex> getDailyIndexListByDate(Date date);

}
