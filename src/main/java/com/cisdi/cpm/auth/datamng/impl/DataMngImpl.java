package com.cisdi.cpm.auth.datamng.impl;

import com.cisdi.cpm.auth.common.ContextPropHolder;
import com.cisdi.cpm.auth.dao.BaseDao;
import com.cisdi.cpm.auth.dao.JdbcTemplateManager;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.exception.SysException;
import com.cisdi.cpm.auth.utils.DateUtils;
import com.cisdi.cpm.auth.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DataMngImpl implements DataMng {
    private static final Logger logger = LoggerFactory.getLogger(DataMngImpl.class);

    @Autowired
    BaseDao bd;

    //---------------------------------------------------------------------
    //	Interface Method
    //----------------------------------------------------------------------
    @Override
    public void execProcedureWithoutOut(String callableProcedure, Object[] in)
            throws BaseException {

        getJdbcTemplate().execute(callableProcedure);
    }

    @Override
    public Object execProcedureWithOut(final String callableProcedure, final Object[] in,
                                       final int out) throws BaseException {

        return getJdbcTemplate().execute(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(Connection conn)
                    throws SQLException {
                CallableStatement cs = conn.prepareCall(callableProcedure);
                //set the last params as output
                cs.registerOutParameter(in.length + 1, out);
                if (in != null) {
                    setParams(cs, 1, in);
                }
                return cs;
            }

        } , new CallableStatementCallback<Object>() {

            @Override
            public Object doInCallableStatement(CallableStatement cs)
                    throws SQLException, DataAccessException {
                cs.execute();
                return cs.getObject(in.length + 1);
            }

        });
    }

    @Override
    public Object execFunction(final String callableFunction, final Object[] in, final int out) throws BaseException {

        return getJdbcTemplate().execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection conn)
                    throws SQLException {
                CallableStatement cs = conn.prepareCall(callableFunction);
                //set CallableStatement params
                if (out != -1) {
                    cs.registerOutParameter(1, out);
                }
                if (in != null) {
                    setParams(cs, 2, in);
                }
                return cs;
            }

        }, new CallableStatementCallback<Object>() {

            @Override
            //Get CallableStatement from CallableStatementCreator and callback
            public Object doInCallableStatement(CallableStatement cs)
                    throws SQLException, DataAccessException {
                cs.execute();
                if (out != -1) {
                    return cs.getObject(1);
                }
                return -1;
            }

        });
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object[] params)
            throws BaseException {
        List<Map<String, Object>> result = bd.queryBySql(ContextPropHolder.getContextProperty("default_db"), sql, params);
        List<Map<String, Object>> _result = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return null;
        }
        for (Map<String, Object> map : result) {
            _result.add(toLower(map));
        }

        return _result;
    }

    @Override
    public List<Map<String, Object>> queryPaged(String sql, String condition,
                                                Object[] params, int page, int pageSize, String orderBy)
            throws BaseException {
        //condition exists
        if (condition != null) {
            sql = sql + " where " + condition;
        }

        //orderBy exists
        if (orderBy != null) {
            sql = sql + " " + orderBy;
        }

        //No page
        if (page == 0 && pageSize == -1) {
            return queryForList(sql, params);
        }

        //Pages
        String head = "select * from (select tt.*, rownum as rn from (";
        String end = ") tt where rownum <= ?) tb where tb.rn >= ?";
        String pagedSql = head + sql + end;

        int startRow = page * pageSize;
        int endRow = (page - 1) * pageSize + 1;

        List<Object> pas = new ArrayList<Object>();
        if (params != null && !"".equals(params)) {
            pas.addAll(Arrays.asList(params));
        }
        pas.add(startRow);
        pas.add(endRow);

        return queryForList(pagedSql, pas.toArray());
    }

    @Override
    public long getCount(String sql, Object[] params) throws BaseException {
        List<Map<String, Object>> result = queryForList(sql, (Object[])params);
        if (result == null || result.size() == 0 || result.size() > 1) {
            return 0;
        }

        Map<String, Object> data = result.get(0);
        if (data.values().size() != 1) {
            return 0;
        }

        for (Object obj : data.values()) {
            try {
                return Long.parseLong(obj.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("[ERROR] - getCount Long Type Convert Exception");
                throw new SysException("Type Transform error");
            }
        }

        return 0;
    }

    @Override
    public List<Map<String, Object>> queryByTableName(String tableName,
                                                      String[] fields, String condition, String orderBy) throws BaseException {
        StringBuffer sb = new StringBuffer("select");
        if (fields == null || fields.length == 0) {
            sb.append(" * ");
        } else {
            int index = 0;
            for (String field : fields) {
                index++;
                sb.append(" ");
                sb.append(field);
                if (index < fields.length) {
                    sb.append(",");
                }
            }
        }
        sb.append(" from ");
        sb.append(tableName);

        if (condition != null) {
            sb.append(" where ");
            sb.append(condition);
        }

        if (orderBy != null) {
            sb.append(" ");
            sb.append(orderBy);
        }

        return queryForList(sb.toString(), null);
    }

    @Override
    public int updateBySql(String sql, Object[] params) throws BaseException {
        return bd.updateBySql(ContextPropHolder.getContextProperty("default_db"), sql, params);
    }

    @Override
    public int updateByTableName(String tableName, Map<String, Object> data, String condition)
            throws BaseException {
        if (data == null || data.size() == 0) {
            return 0;
        }
        if (!data.containsKey("id")) {
            logger.warn("Data do not contain ID");
            return 0;
        }

        String id = (String) data.get("id");
        data.remove("id");
        if (data.size() == 0) {
            logger.warn("ID not extis when updating table!");
            return 0;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("update ");
        sb.append(tableName);
        sb.append(" set ");
        sb.append(buildUpdateData(data));

        sb.append(" where id='" + id + "'");

        if (condition != null) {
            sb.append(" and ");
            sb.append(condition);
        }

        return updateBySql(sb.toString(), null);
    }

    @Override
    public int insertBySql(String sql, Object[] params) throws BaseException {
        return bd.insertBySql(ContextPropHolder.getContextProperty("default_db"), sql, params);
    }

    @Override
    public String insertByTableName(String tableName, Map<String, Object> data)
            throws BaseException {
        if (data == null || data.size() == 0) {
            return null;
        }

        String id = StrUtils.getUUID();
        data.put("id", id);
        Map<String, String> insertData = buildInsertData(data);
        StringBuffer sb = new StringBuffer();
        sb.append("insert into ");
        sb.append(tableName);
        sb.append("(");
        sb.append(insertData.get("fields"));
        sb.append(") values (");
        sb.append(insertData.get("values"));
        sb.append(")");

        if (insertBySql(sb.toString(), null) > 0) {
            return id;
        }
        return null;
    }

    @Override
    public int deleteBySql(String sql, Object[] params) throws BaseException {
        return bd.deleteBySql(ContextPropHolder.getContextProperty("default_db"), sql, params);
    }

    @Override
    public int[] batchUpdate(String sql, final List<Object[]> params)
            throws BaseException {

        //Build batch preparedStatement setter
        BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter(){
            public void setValues(PreparedStatement ps, int si) throws SQLException{
                Object[] param = params.get(si);	//The params array of every sql
                //set pareparedStatement params for every sql
                for (int pi = 0; pi < param.length; pi++) {
                    //Handle date type
                    if (param[pi] instanceof Date) {
                        ps.setDate(pi+1, new java.sql.Date(((java.util.Date)param[pi]).getTime()));
                    } else {
                        ps.setObject(pi+1, param[pi]);
                    }
                }
            }

            //Get updated record number
            public int getBatchSize() {
                return params.size();
            }};

        int[] index;
        try {
            index = getJdbcTemplate().batchUpdate(sql, setter);
        } catch (BaseException ex) {
            throw new SysException(ex.getMessage());
        }

        return index;
    }

    @Override
    public int[] batchUpdate(String[] sqls) throws BaseException {
        return getJdbcTemplate().batchUpdate(sqls);
    }

    //------------------------------------------------------------------------
    // Private Method
    //-------------------------------------------------------------------------

    /**
     * Make Map's key to lower
     *
     * @param data
     * @throws BaseException
     */
    private Map<String, Object> toLower(Map<String, Object> data) throws BaseException {
        Map<String, Object> _data = new HashMap<String, Object>();
        for (String key : data.keySet()) {
            _data.put(key.toLowerCase(), data.get(key));
        }
        return _data;
    }

    /**
     *
     * @param data
     * @return
     * @throws BaseException
     */
    private String buildUpdateData(Map<String, Object> data) throws BaseException {
        StringBuffer _sb = new StringBuffer("");
        int index = 0;
        for (Map.Entry<String, Object> entity : data.entrySet()) {
            index++;
            _sb.append(entity.getKey());
            _sb.append("=");
            if (entity.getValue() == null) {
                _sb.append(entity.getValue());
            } else {
                if (entity.getValue() instanceof Date) {
                    _sb.append("to_date('" + DateUtils.formatDate((Date) entity.getValue(), "yyyy-MM-dd HH:mm:ss") + "', 'yyyy-mm-dd hh24:mi:ss')");
                } else {
                    _sb.append("'" + entity.getValue() + "'");
                }
            }
            if (index < data.size()) {
                _sb.append(",");
            }
        }

        return _sb.toString();
    }

    private Map<String, String> buildInsertData(Map<String, Object> data) throws BaseException {
        StringBuffer _fields = new StringBuffer();
        StringBuffer _values = new StringBuffer();
        int index = 0;
        for (Map.Entry<String, Object> entity : data.entrySet()) {
            index++;
            _fields.append(entity.getKey());
            if (entity.getValue() instanceof Date) {
                _values.append("to_date('" + DateUtils.formatDate((Date) entity.getValue(), "yyyy-MM-dd HH:mm:ss") + "', 'yyyy-mm-dd hh24:mi:ss')");
            } else {
                if (entity.getValue() == null ) {
                    _values.append(entity.getValue());
                } else {
                    _values.append("'" + entity.getValue() + "'");
                }
            }
            if (index < data.size()) {
                _fields.append(",");
                _values.append(",");
            }
        }
        Map<String, String> result = new HashMap<String, String>();
        result.put("fields", _fields.toString());
        result.put("values", _values.toString());
        return result;
    }

    /**
     * Set Function CallableStatement input params
     * @param cs
     * @param params
     * @return
     * @throws BaseException
     */
    private void setParams(CallableStatement cs, int order, Object[] in)
            throws BaseException {
        for (Object param : in) {
            if (param instanceof String) {
                try {
                    cs.setString(order, (String) param);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    throw new SysException("String Type Convertion Exception", e);
                }
                order++;
            }
            else if (param instanceof Long) {
                try {
                    cs.setLong(order, (Long) param);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    throw new SysException("Long Type Convertion Exception", e);
                }
                order++;
            }
            else if (param instanceof Integer) {
                try {
                    cs.setInt(order, (Integer) param);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    throw new SysException("Integer Type Convertion Exception", e);
                }
                order++;
            }
        }
    }

    //get JdbcTemplate
    private JdbcTemplate getJdbcTemplate() {
        return JdbcTemplateManager.instance().getTemplate(ContextPropHolder.getContextProperty("default_db"));
    }
}

