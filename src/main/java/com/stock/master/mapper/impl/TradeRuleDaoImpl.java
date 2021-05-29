package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.TradeRuleDao;
import com.stock.master.model.po.TradeRule;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.utils.SqlCondition;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TradeRuleDaoImpl extends BaseDao implements TradeRuleDao {

    private static final String SELECT_SQL = "select id, rate, state, description, create_time as createTime, update_time as updateTime from trade_rule where 1 = 1";

    @Override
    public TradeRule getTradeRuleByStockCode(String stockCode) {
         List<TradeRule> list = jdbcTemplate.query("select tr.id, tr.rate, tr.state"
                + " from trade_rule tr, trade_stock_info_rule tsr, stock_info si where tr.id = tsr.rule_id and tsr.stock_code = si.code and si.code = ? and tr.state = 1 and tsr.state = 1",
                new String[] { stockCode }, BeanPropertyRowMapper.newInstance(TradeRule.class));
         return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public PageVo<TradeRule> get(PageParam pageParam) {
        SqlCondition dataSqlCondition = new SqlCondition(
                TradeRuleDaoImpl.SELECT_SQL,
                pageParam.getCondition());

        int totalRecords = jdbcTemplate.queryForObject(dataSqlCondition.getCountSql(),
                dataSqlCondition.toArgs(), Integer.class);

        dataSqlCondition.addSql(" limit ?, ?");
        dataSqlCondition.addPage(pageParam.getStart(), pageParam.getLength());

        List<TradeRule> list = jdbcTemplate.query(dataSqlCondition.toSql(),
                dataSqlCondition.toArgs(), BeanPropertyRowMapper.newInstance(TradeRule.class));
        return new PageVo<>(list, totalRecords);
    }

}
