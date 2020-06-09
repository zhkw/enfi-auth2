package com.cisdi.cpm.auth.dao.impl;

import com.cisdi.cpm.auth.dao.BaseDao;
import com.cisdi.cpm.auth.dao.JdbcTemplateManager;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.exception.SysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class BaseDaoImpl implements BaseDao {

    private static final Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);

    @Override
    public List<Map<String, Object>> queryBySql(String dsName, String sql, Object[] params)
            throws BaseException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dsName);

        logger.debug("[SQL]-" + sql + "-[参数]-" + Arrays.toString(params));
        if (params == null) {
            return jdbcTemplate.queryForList(sql);
        }
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public int updateBySql(String dsName, String sql, Object[] params)
            throws BaseException {
        return executeUpdate(dsName, sql, params);
    }

    @Override
    public int insertBySql(String dsName, String sql, Object[] params)
            throws BaseException {
        return executeUpdate(dsName, sql, params);
    }

    @Override
    public int deleteBySql(String dsName, String sql, Object[] params)
            throws BaseException {
        return executeUpdate(dsName, sql, params);
    }

    private int executeUpdate(String dsName, String sql, Object[] params) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dsName);

        logger.debug("[SQL]-" + sql + "-[参数]-" + Arrays.toString(params));
        try {
            if (params == null) {
                return jdbcTemplate.update(sql);
            }
            return jdbcTemplate.update(sql, params);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SysException("[executeUpdate] - update Error!");
        }
    }

    private JdbcTemplate getJdbcTemplate(String dsName) {
        JdbcTemplate jdbcTemplate = JdbcTemplateManager.instance().getTemplate(dsName);
        if (jdbcTemplate == null) {
            logger.error("[ERROR]-获取JdbcTemplate失败");
            throw new SysException("JdbcTemplate 获取失败");
        }

        return jdbcTemplate;
    }
}
