package com.stock.master.mapper.impl;

import com.stock.master.mapper.BaseDao;
import com.stock.master.mapper.StockInfoDao;
import com.stock.master.model.po.StockInfo;
import com.stock.master.model.vo.PageParam;
import com.stock.master.model.vo.PageVo;
import com.stock.master.utils.SqlCondition;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StockInfoDaoImpl extends BaseDao implements StockInfoDao {

    private static final String SELECT_SQL = "select id, code, name, exchange, abbreviation, state, type, create_time as createTime, update_time as updateTime from stock_info where 1 = 1";

    @Override
    public void add(List<StockInfo> list) {
        jdbcTemplate.batchUpdate(
                "insert into stock_info(code, name, exchange, abbreviation, state, type) values(?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        StockInfo stockInfo = list.get(i);
                        ps.setString(1, stockInfo.getCode());
                        ps.setString(2, stockInfo.getName());
                        ps.setString(3, stockInfo.getExchange());
                        ps.setString(4, stockInfo.getAbbreviation());
                        ps.setInt(5, stockInfo.getState());
                        ps.setInt(6, stockInfo.getType());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
    }

    @Override
    public void update(List<StockInfo> list) {
        jdbcTemplate.batchUpdate(
                "update stock_info set name = ?, abbreviation = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        StockInfo stockInfo = list.get(i);
                        ps.setString(1, stockInfo.getName());
                        ps.setString(2, stockInfo.getAbbreviation());
                        ps.setInt(3, stockInfo.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
    }

    @Override
    public PageVo<StockInfo> get(PageParam pageParam) {
        SqlCondition dataSqlCondition = new SqlCondition(
                StockInfoDaoImpl.SELECT_SQL,
                pageParam.getCondition());

        int totalRecords = jdbcTemplate.queryForObject(dataSqlCondition.getCountSql(),
                dataSqlCondition.toArgs(), Integer.class);

        dataSqlCondition.addSql(" limit ?, ?");
        dataSqlCondition.addPage(pageParam.getStart(), pageParam.getLength());

        List<StockInfo> list = jdbcTemplate.query(dataSqlCondition.toSql(),
                dataSqlCondition.toArgs(), BeanPropertyRowMapper.newInstance(StockInfo.class));
        return new PageVo<>(list, totalRecords);
    }

    @Override
    public StockInfo getStockByFullCode(String code) {
        List<StockInfo> list = jdbcTemplate.query(StockInfoDaoImpl.SELECT_SQL + " and concat(exchange, code) = ?",
                new String[] { code }, BeanPropertyRowMapper.newInstance(StockInfo.class));
        return list.isEmpty() ? null : list.get(0);
    }

}
