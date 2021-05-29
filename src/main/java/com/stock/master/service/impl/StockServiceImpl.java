package com.stock.master.service.impl;

import com.stock.master.mapper.DailyIndexDao;
import com.stock.master.mapper.StockInfoDao;
import com.stock.master.mapper.StockLogDao;
import com.stock.master.model.po.DailyIndex;
import com.stock.master.model.po.StockInfo;
import com.stock.master.model.po.StockLog;
import com.stock.master.model.vo.DailyIndexVo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.parser.DailyIndexParser;
import com.stock.master.service.StockCrawlerService;
import com.stock.master.service.StockService;
import com.stock.master.utils.StockConsts;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    private static final String LIST_MESSAGE = "'list' must not be null";

    @Autowired
    private StockInfoDao stockInfoDao;

    @Autowired
    private StockLogDao stockLogDao;

    @Autowired
    private DailyIndexDao dailyIndexDao;

    @Autowired
    private StockCrawlerService stockCrawlerService;

    @Autowired
    private DailyIndexParser dailyIndexParser;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public List<StockInfo> getAll() {
        PageParam pageParam = new PageParam();
        pageParam.setStart(0);
        pageParam.setLength(Integer.MAX_VALUE);
        PageVo<StockInfo> pageVo = stockInfoDao.get(pageParam);
        return pageVo.getData();
    }

    @Override
    public List<StockInfo> getAllListed() {
        return getAll().stream().filter(stockInfo ->
            stockInfo.isValid() && stockInfo.isA()
        ).collect(Collectors.toList());
    }

    @Override
    public void addStockLog(List<StockLog> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockLogDao.add(list);
        }
    }

    @CacheEvict(value = StockConsts.CACHE_KEY_DATA_STOCK, allEntries = true)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void update(List<StockInfo> needAddedList, List<StockInfo> needUpdatedList, List<StockLog> stockLogList) {
        if (needAddedList != null) {
            add(needAddedList);
        }
        if (needUpdatedList != null) {
            update(needUpdatedList);
        }
        if (stockLogList != null) {
            addStockLog(stockLogList);
        }
        if (needAddedList != null && !needAddedList.isEmpty()) {
            List<String> newCodeList = needAddedList.stream().map(StockInfo::getCode)
                    .collect(Collectors.toList());
            stockLogDao.setStockIdByCodeType(newCodeList, StockConsts.StockLogType.New.value());
        }
    }

    private void add(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.add(list);
        }
    }

    private void update(List<StockInfo> list) {
        Assert.notNull(list, StockServiceImpl.LIST_MESSAGE);
        if (!list.isEmpty()) {
            stockInfoDao.update(list);
        }
    }

    @Override
    public void saveDailyIndexToFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        list.forEach(stockInfo -> {
            logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".txt");
                if (file.length() < 5 * 1024) {
                    String content = stockCrawlerService.getHistoryDailyIndexsString(stockInfo.getCode());
                    try (FileWriter out = new FileWriter(file)) {
                        FileCopyUtils.copy(content, out);
                    }
                }
            } catch (Exception e) {
                logger.error("get daily index error {} {}", stockInfo.getExchange(), stockInfo.getCode(), e);
            }
        });
    }

    @Override
    public void saveDailyIndexFromFile(String rootPath) {
        List<StockInfo> list = getAll().stream().filter(StockInfo::isA).collect(Collectors.toList());

        File root = new File(rootPath);

        AtomicInteger atomicInteger = new AtomicInteger(0);

        list.forEach(stockInfo -> {
            logger.info("start save {}: {}", stockInfo.getName(), stockInfo.getCode());
            try {
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        atomicInteger.incrementAndGet();
                        handleStockDaily(root, stockInfo);
                    } finally {
                        atomicInteger.decrementAndGet();
                    }
                });
            } catch(RejectedExecutionException ex) {
                logger.error("task rejected {} {}", stockInfo.getExchange(), stockInfo.getCode());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                handleStockDaily(root, stockInfo);
            }
        });

        while (atomicInteger.get() <= 0) {
            logger.info("sub task is not completed");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

    }

    private void handleStockDaily(File root, StockInfo stockInfo) {
        try {
            File file = new File(root, stockInfo.getExchange() + "/" + stockInfo.getCode() + ".txt");
            try (FileReader in = new FileReader(file)) {
                String content = FileCopyUtils.copyToString(in);
                List<DailyIndex> dailyIndexList = dailyIndexParser.parseHistoryDailyIndexList(content);
                dailyIndexList.forEach(dailyIndex -> dailyIndex.setStockInfoId(stockInfo.getId()));
                dailyIndexDao.save(dailyIndexList);
            }
        } catch (Exception e) {
            logger.error("save daily index error {} {}", stockInfo.getExchange(), stockInfo.getCode(), e);
        }
    }

    @Override
    public void saveDailyIndex(DailyIndex dailyIndex) {
        dailyIndexDao.save(dailyIndex);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveDailyIndex(List<DailyIndex> list) {
        dailyIndexDao.save(list);
    }

    @Override
    public PageVo<StockInfo> getStockList(PageParam pageParam) {
        return stockInfoDao.get(pageParam);
    }

    @Cacheable(value = StockConsts.CACHE_KEY_DATA_STOCK, key = "#code")
    @Override
    public StockInfo getStockByFullCode(String code) {
        StockInfo stockInfo = stockInfoDao.getStockByFullCode(code);
        if (stockInfo == null) {
            stockInfo = new StockInfo();
            stockInfo.setAbbreviation("wlrzq");
            stockInfo.setCode(code);
            stockInfo.setName("未录入证券");
            stockInfo.setExchange(StockConsts.Exchange.SH.getName());
            stockInfo.setState(StockConsts.StockState.Terminated.value());
            stockInfo.setType(StockConsts.StockType.A.value());
        }
        return stockInfo;
    }

    @Override
    public PageVo<DailyIndexVo> getDailyIndexList(PageParam pageParam) {
        pageParam.getCondition().put("date", DateUtils.formatDate(new Date(), "yyyy-MM-dd") );
        return dailyIndexDao.getDailyIndexList(pageParam);
    }

    @Override
    public List<DailyIndex> getDailyIndexListByDate(Date date) {
        return dailyIndexDao.getDailyIndexListByDate(date);
    }

}
