package com.cisdi.cpm.auth.datamng.impl;

import com.cisdi.cpm.auth.datamng.CommonService;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.BaseException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("authCommonService")
public class CommonServiceImpl implements CommonService {
    @Resource
    DataMng dm;

    @Override
    public List<Map<String, Object>> searchBySql(String sql, String[] attrs, String keyword, String condition,
                                                 Object[] params, int page, int pageSize, String orderBy)
            throws BaseException {
        return dm.queryPaged(buildQuerySql(sql, attrs, keyword, condition), null, params, page, pageSize, orderBy);
    }

    @Override
    public long getSearchCount(String sql, String[] attrs, String keyword,
                               String condition, String[] params) throws BaseException {
        return dm.getCount(buildQuerySql(sql, attrs, keyword, condition), params);
    }

    private String handleKeyword(String keyword) throws BaseException {
        if (keyword != null && !"".equals(keyword)) {
            return keyword;
        }

        return null;
    }

    private String buildCondition(String[] attrs, String keyword) throws BaseException {
        if (keyword == null) {
            return "";
        }
        String condition = "";
        int index = 0;
        for (String attr : attrs) {
            index++;
            condition += attr + " like '%" + keyword + "%'";
            if (index < attrs.length) {
                condition += " or ";
            }
        }

        return condition;
    }

    private String buildQuerySql(String sql, String[] attrs, String keyword, String condition) throws BaseException {
        //build query condition
        sql += " where 1=1";

        String Qcondition = buildCondition(attrs, handleKeyword(keyword));

        if (!"".equals(Qcondition)) {
            sql += " and " + Qcondition;
        }

        if (condition != null) {
            sql += " and " + condition;
        }

        return sql;
    }

}
