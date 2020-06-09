package com.cisdi.cpm.auth.dao;

import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.exception.SysException;
import com.cisdi.cpm.auth.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JdbcTemplateManager {
    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateManager.class);

    //support multiple DataSource
    private static Map<String, JdbcTemplate> jdbcTemplates = new HashMap<String, JdbcTemplate>();

    //singleton
    private static JdbcTemplateManager instance = null;
    private JdbcTemplateManager() {}
    public static JdbcTemplateManager instance() {
        if (instance == null) {
            instance = new JdbcTemplateManager();
        }
        return instance;
    }

    //Get JdbcTemplate by dataSource Name
    public JdbcTemplate getTemplate(String dsName) {
        JdbcTemplate jdbcTemplate = jdbcTemplates.get(dsName);
        if (jdbcTemplate == null) {
            createJdbcTemplate(dsName);
        }

        return jdbcTemplates.get(dsName);
    }

    /**
     * Get Connection from DataSource named dsName
     *
     * @param dsName
     * @return
     */
    public Connection getConn(String dsName) throws BaseException {
        JdbcTemplate jdbcTemplate = jdbcTemplates.get(dsName);
        if (jdbcTemplate == null) {
            createJdbcTemplate(dsName);
        }
        try {
            return jdbcTemplates.get(dsName).getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("[ERROR]-Get DataSource:" + dsName + "Error!");
            throw new SysException("Get DataSource:" + dsName + "Error!");
        }
    }

    //Create new JdbcTemplate and put into JdbcTemplates
    @SuppressWarnings("static-access")
    private synchronized void createJdbcTemplate(String dsName) {
        DataSource ds = (DataSource) SpringUtils.instance().getBean(dsName);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplates.put(dsName, jdbcTemplate);

        logger.debug("创建JdbcTempate-[数据源]-" + dsName);
    }

}

