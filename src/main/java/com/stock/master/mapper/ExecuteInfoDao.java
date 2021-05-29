package com.stock.master.mapper;

import com.stock.master.model.po.ExecuteInfo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.model.vo.TaskVo;

import java.util.List;

public interface ExecuteInfoDao {

    List<ExecuteInfo> getByTaskIdAndState(int[] id, int value);

    void update(ExecuteInfo executeInfo);

    PageVo<TaskVo> get(PageParam pageParam);

    void updateState(int state, int id);

}
