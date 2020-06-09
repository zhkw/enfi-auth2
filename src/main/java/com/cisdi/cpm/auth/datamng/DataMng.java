package com.cisdi.cpm.auth.datamng;

import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;
import java.util.Map;

public interface DataMng {
    /**
     * Execute procedure without parameter, procedure type like "{call procedure(?,?)}".
     * All params are input, and no output.
     *
     * @param callProcedure
     * 	callable procedure,
     * @param in
     * 	Input Params
     *
     * @throws BaseException
     */
    public void execProcedureWithoutOut(final String callableProcedure, final Object[] in) throws BaseException;

    /**
     * Execute procedure with output params, procedure type like "{call callproceure(?,?)}", and the last param is return value.
     *
     * @param callableProcedure
     * 	callable procedure,
     * @param in
     * 	Input params
     * @param out
     * 	Oupt params type(All type is the type of Integer)
     * @return
     * @throws BaseException
     */
    public Object execProcedureWithOut(final String callableProcedure, final Object[] in, final int out) throws BaseException;

    /**
     * Execute Function, Func Type like "{? = call func(?,?)}", the first params is the return, others are input params.
     *
     * @param callableFunc
     * @param in
     * 	null : the function have no params.
     * @param out
     * 	-1 : the function have no return value.
     * @return
     * 	If out = -1, return -1
     * @throws BaseException
     */
    public Object execFunction(final String callableFunc, final Object[] in, final int out) throws BaseException;

    /**
     * Query for list with default dsName
     * The key of result is the lower type.
     *
     * @param sql
     * @param params
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> queryForList(String sql, Object[] params) throws BaseException;

    /**
     * Query with tableName by condition and orderBy
     * If fields is null, query all
     *
     *
     * @param tableName
     * @param fields
     * @param condition
     * @param orderBy
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> queryByTableName(String tableName, String[] fields, String condition, String orderBy) throws BaseException;

    /**
     * Query Bysql with default dsName, and make page for the result, order by according orderBy.
     * If sql contains multiple table, please use alias for every table.
     *
     * @param sql
     * 	The basic sql, of course, you can make the whole sql and let condition null
     * @param condition
     * 	The condition of sql, the purpose of condition is to make separation of select sql and condition, and make the whole sql not too long.
     * 	Of course, condition can be null, and make it into sql
     * @param params
     * 	The placeholder of the whole sql
     * @param page
     * 	The current page
     * @param pageSize
     * 	The pageSize of per page
     * @param orderBy
     * 	Example:order by xx desc
     * 	orderBy can be null, and make it into sql
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> queryPaged(String sql, String condition, Object[] params, int page, int pageSize, String orderBy) throws BaseException;

    /**
     * contain count
     *
     * @param sql
     * @param params
     * @return
     * @throws BaseException
     */
    public long getCount(String sql, Object[] params) throws BaseException;

    /**
     * Update by sql with default dsName
     *
     * @param sql
     * @param params
     * @return
     * @throws BaseException
     */
    public int updateBySql(String sql, Object[] params) throws BaseException;

    /**
     * Update By TableName with default dsName
     *
     * The data is used to update tableName, and the key is the field Name, the value is the updated value.
     *  If the key or value of data is not suitable for table, the exception will be generated,
     *  in other words, method updateByTableName do not make any error check.
     *  And the fields of table not in data will not be updated
     *
     * @param tableName
     *
     * @param data
     * 	The updated data
     * @param condition
     * 	The update conditon
     * @return
     * @throws BaseException
     */
    public int updateByTableName(String tableName, Map<String, Object> data, String condition) throws BaseException;

    /**
     * Insert by sql with default dsName
     *
     * @param sql
     * @param params
     * @return
     * @throws BaseException
     */
    public int insertBySql(String sql, Object[] params) throws BaseException;

    /**
     * Insert By tableName
     *
     * The data is used to insert into tableName, and the key is the field Name, the value is the updated value.
     * If the key or value of data is not suitable for table, the exception will be generated,
     * in other words, method updateByTableName do not make any error check.
     * And the fields of table not in data will not be inserted into.
     *
     * @param tableName
     * @param data
     * @return
     * @throws BaseException
     */
    public String insertByTableName(String tableName, Map<String, Object> data) throws BaseException;

    /**
     * delete by sql with default dsName, and this is physical delete.
     * If you want to make logical delete, please use update method.
     *
     * @param sql
     * @param params
     * @return
     * @throws BaseException
     */
    public int deleteBySql(String sql, Object[] params) throws BaseException;

    /**
     * Batch execute with the same sql, just the params of each sql is different.
     *
     * @param sql
     * 	Common sql(update/delete/insert)
     * @param params
     * 	The sql params for each sql of batch
     * @return
     * @throws BaseException
     */
    public int[] batchUpdate(String sql, List<Object[]> params) throws BaseException;

    /**
     * Batch execute with the different sql, in other words, each sql in the batch is possibly different
     *
     * @param sql
     * 	sql array
     * @return
     * @throws BaseException
     */
    public int[] batchUpdate(String[] sql) throws BaseException;


}

