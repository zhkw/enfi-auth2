package com.cisdi.cpm.auth.datamng;

import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;
import java.util.Map;

public interface CommonService {
    /**
     * Search by sql, if sql contains multiple tables, alias of each table should be assigned,
     * and attrs and orderBy should add suffix of alias.
     * Sql should be simple statement, do not contains any condition.
     *
     * @param sql
     * @param attrs
     * @param condition
     * @param page
     * @param pageSize
     * @param orderBy
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> searchBySql(String sql, String[] attrs, String keyword, String condition,
                                                 Object[] params, int page, int pageSize, String orderBy) throws BaseException;

    /**
     * get the search result count, buildCondition like 'seachBySql'
     *
     * @param sql
     * @param attrs
     * @param keyword
     * @param condition
     * @return
     * @throws BaseException
     */
    public long getSearchCount(String sql, String[] attrs, String keyword, String condition, String[] params) throws BaseException;
}
