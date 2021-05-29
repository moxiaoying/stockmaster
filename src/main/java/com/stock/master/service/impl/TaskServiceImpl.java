package com.stock.master.service.impl;

import com.stock.master.config.SpringUtil;
import com.stock.master.exception.ServiceException;
import com.stock.master.mapper.ExecuteInfoDao;
import com.stock.master.model.po.*;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.model.vo.TaskVo;
import com.stock.master.service.*;
import com.stock.master.trategy.handle.StrategyHandler;
import com.stock.master.utils.DecimalUtil;
import com.stock.master.utils.StockConsts;
import com.stock.master.utils.StockUtil;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private Map<String, BigDecimal> lastPriceMap = new HashMap<>();

    @Autowired
    private HolidayCalendarService holidayCalendarService;

    @Autowired
    private ExecuteInfoDao executeInfoDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MessageService messageServicve;

    @Autowired
    private TradeStrategyService tradeStrategyService;

    @Autowired
    private StockSelectedService stockSelectedService;

    @Override
    public List<ExecuteInfo> getPendingTaskListById(int... id) {
        return executeInfoDao.getByTaskIdAndState(id, StockConsts.TaskState.Pending.value());
    }

    @Override
    public void executeTask(ExecuteInfo executeInfo) {
        executeInfo.setStartTime(new Date());
        executeInfo.setMessage("");
        int id = executeInfo.getTaskId();
        Task task = Task.valueOf(id);
        try {
            switch (task) {
            case BeginOfYear:
                holidayCalendarService.updateCurrentYear();
                break;
            case EndOfYear:
                break;
            case BeginOfDay:
                lastPriceMap.clear();
                break;
            case EndOfDay:
                break;
            case UpdateOfStock:
                runUpdateOfStock();
                break;
            case UpdateOfStockState:
                runUpdateOfStockState();
                break;
            case UpdateOfDailyIndex:
                runUpdateOfDailyIndex();
                break;
            case Ticker:
                runTicker();
                break;
            case TradeTicker:
                runTradeTicker();
                break;
            default:
                break;
            }
        } catch (Exception e) {
            executeInfo.setMessage(e.getMessage());
            logger.error("task {} error", executeInfo.getTaskId(), e);

            String body = String.format("task: %s, error: %s", task.getName(), e.getMessage());
            messageServicve.send(body);
        }

        executeInfo.setCompleteTime(new Date());
        executeInfoDao.update(executeInfo);
    }

    private void runUpdateOfStock() {
        List<StockInfo> list = stockService.getAll().stream().filter(v -> !v.isIndex()).collect(Collectors.toList());
        Map<String, List<StockInfo>> dbStockMap = list.stream().collect(Collectors.groupingBy(StockInfo::getCode));

        ArrayList<StockInfo> needAddedList = new ArrayList<>();
        ArrayList<StockInfo> needUpdatedList = new ArrayList<>();
        ArrayList<StockLog> stockLogList = new ArrayList<>();

        final Date date = new Date();

        List<StockInfo> crawlerList = stockCrawlerService.getStockList();
        for (StockInfo stockInfo : crawlerList) {
            StockConsts.StockLogType stocLogType = null;
            List<StockInfo> stockGroupList = dbStockMap.get(stockInfo.getCode());
            String oldValue = null;
            String newValue = null;
            if (stockGroupList == null) {
                stocLogType = StockConsts.StockLogType.New;
                oldValue = "";
                newValue = stockInfo.getName();
            } else {
                StockInfo stockInfoInDb = stockGroupList.get(0);
                if (!stockInfo.getName().equals(stockInfoInDb.getName())
                        && StockUtil.isOriName(stockInfo.getName())) {
                    stocLogType = StockConsts.StockLogType.Rename;
                    oldValue = stockInfoInDb.getName();
                    newValue = stockInfo.getName();
                    stockInfo.setId(stockInfoInDb.getId());
                }
            }

            if (stocLogType != null) {
                StockLog stockLog = new StockLog(stockInfo.getId(), date, stocLogType.value(), oldValue, newValue);
                if (stocLogType == StockConsts.StockLogType.New) {
                    needAddedList.add(stockInfo);
                } else {
                    needUpdatedList.add(stockInfo);
                }
                stockLogList.add(stockLog);
            }
        }

        stockService.update(needAddedList, needUpdatedList, stockLogList);
    }

    private void runUpdateOfStockState() {
        List<StockInfo> list = stockService.getAllListed();

        for (StockInfo stockInfo : list) {
            StockConsts.StockState state = stockCrawlerService.getStockState(stockInfo.getCode());
            if (stockInfo.getState() != state.value()) {
                StockConsts.StockLogType stockLogType = null;
                switch (state) {
                case Terminated:
                    stockLogType = StockConsts.StockLogType.Terminated;
                    break;
                default:
                    throw new ServiceException("未找到状态" + state);
                }
                StockLog stockLog = new StockLog(stockInfo.getId(), new Date(), stockLogType.value(),
                        String.valueOf(stockInfo.getState()), String.valueOf(state.value()));
                stockInfo.setState(state.value());
                stockService.update(null, Arrays.asList(stockInfo), Arrays.asList(stockLog));
            }
        }
    }

    private void runUpdateOfDailyIndex() {
        List<StockInfo> list = stockService.getAll().stream()
                .filter(stockInfo -> (stockInfo.isA() || stockInfo.isIndex()) && stockInfo.isValid())
                .collect(Collectors.toList());

        Date date = new Date();

        List<DailyIndex> dailyIndexList = stockService.getDailyIndexListByDate(date);
        List<Integer> stockIdList = dailyIndexList.stream().map(DailyIndex::getStockInfoId).collect(Collectors.toList());

        final String currentDateStr = DateUtils.formatDate(date, "yyyy-MM-dd");
        for (StockInfo stockInfo : list) {
            if (stockIdList.contains(stockInfo.getId())) {
                continue;
            }
            DailyIndex dailyIndex = stockCrawlerService.getDailyIndex(stockInfo.getExchange() + stockInfo.getCode());
            if (dailyIndex != null
                    && DecimalUtil.bg(dailyIndex.getOpeningPrice(), BigDecimal.ZERO)
                    && dailyIndex.getTradingVolume() > 0
                    && DecimalUtil.bg(dailyIndex.getTradingValue(), BigDecimal.ZERO)
                    && currentDateStr.equals(DateUtils.formatDate(dailyIndex.getDate(), "yyyy-MM-dd"))) {
                dailyIndex.setStockInfoId(stockInfo.getId());
                stockService.saveDailyIndex(dailyIndex);
            }
        }
    }

    private void runTicker() {
        List<StockSelected> configList = stockSelectedService.getList();
        for (StockSelected stockSelected : configList) {
            String code = stockSelected.getCode();
            DailyIndex dailyIndex = stockCrawlerService.getDailyIndex(code);
            if (dailyIndex == null) {
                continue;
            }
            if (lastPriceMap.containsKey(code)) {
                BigDecimal lastPrice = lastPriceMap.get(code);
                double rate = Math.abs(StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(), lastPrice).doubleValue());
                if (Double.compare(rate, stockSelected.getRate().doubleValue()) >= 0) {
                    lastPriceMap.put(code, dailyIndex.getClosingPrice());
                    String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                    String body = String.format("%s:当前价格:%.02f, 涨幅%.02f%%", name,
                        dailyIndex.getClosingPrice().doubleValue(),
                        StockUtil.calcIncreaseRate(dailyIndex.getClosingPrice(),
                                dailyIndex.getPreClosingPrice()).movePointRight(2).doubleValue());
                    messageServicve.send(body);
                }
            } else {
                lastPriceMap.put(code, dailyIndex.getPreClosingPrice());
                String name = stockService.getStockByFullCode(StockUtil.getFullCode(code)).getName();
                String body = String.format("%s:当前价格:%.02f", name, dailyIndex.getClosingPrice().doubleValue());
                messageServicve.send(body);
            }
        }
    }

    private void runTradeTicker() {
        List<TradeStrategy> list = tradeStrategyService.getAll();
        list.forEach(v -> {
            if (v.getState() == StockConsts.TradeState.Valid.value()) {
                String beanName = v.getBeanName();
                StrategyHandler strategyHandler = SpringUtil.getBean(beanName, StrategyHandler.class);
                try {
                    strategyHandler.handle();
                } catch (Exception e) {
                    logger.error("strategyHandler {} error", v.getName(), e);
                }
            }
        });
    }

    @Override
    public PageVo<TaskVo> getAllTask(PageParam pageParam) {
        return executeInfoDao.get(pageParam);
    }

    @Override
    public void changeTaskState(int state, int id) {
        executeInfoDao.updateState(state, id);
    }

}
