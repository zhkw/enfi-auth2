package com.cisdi.cpm.auth.dao;

import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;
import java.util.Map;

public interface BaseDao {
    public List<Map<String, Object>> queryBySql(String dbId, String sql, Object[] params) throws BaseException;

    public int updateBySql(String dbId, String sql, Object[] params) throws BaseException;

    public int insertBySql(String dbId, String sql, Object[] params) throws BaseException;

    public int deleteBySql(String dbId, String sql, Object[] params) throws BaseException;
}
