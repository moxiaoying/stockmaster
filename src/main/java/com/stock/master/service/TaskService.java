package com.stock.master.service;


import com.stock.master.model.po.ExecuteInfo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.model.vo.TaskVo;

import java.util.List;

public interface TaskService {

    List<ExecuteInfo> getPendingTaskListById(int... id);

    void executeTask(ExecuteInfo executeInfo);

    PageVo<TaskVo> getAllTask(PageParam pageParam);

    void changeTaskState(int state, int id);

}
