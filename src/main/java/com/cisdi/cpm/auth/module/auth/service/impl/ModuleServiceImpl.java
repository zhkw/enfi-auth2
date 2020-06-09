package com.cisdi.cpm.auth.module.auth.service.impl;

import com.cisdi.cpm.auth.common.contant.ModuleType;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.module.auth.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("authModuleService")
public class ModuleServiceImpl implements ModuleService {
    private static final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Resource
    DataMng dm;

    @Override
    public String addModule(Map<String, Object> module) throws BaseException {
        String condition = " 1=1 ";
        module.put("createTime", new Date());
        module.put("updateTime", new Date());
        if (module.get("parentid") == null || "".equals(((String)module.get("parentid")).trim())) {
            module.put("parentid", null);
        }

        if (null == module.get("parentid")) {
            condition += " and parentid is null";
        } else {
            condition += " and parentid ='" + module.get("parentId") + "'";
        }

        //get the max indexnum of the same parent
        if (module.get("indexnum") == null) {
            int maxIndex = 0;
            String sql_maxIndex = "select max(indexnum) as maxIndex from ctp_module where " + condition;
            List<Map<String, Object>> result = dm.queryForList(sql_maxIndex, null);
            if (result != null && result.size() > 0 && result.get(0).get("maxindex") != null) {
                maxIndex = Integer.parseInt(result.get(0).get("maxindex").toString());
            }
            module.put("indexnum", maxIndex + 1);
        }

        return dm.insertByTableName("ctp_module", module);
    }

    @Override
    public boolean deleteModule(String moduleId) throws BaseException {
        //Get module node and its children nodes
        String sql = "select id from ctp_module start with id=? connect by parentid = prior id";
        List<Map<String, Object>> modules = dm.queryForList(sql, new String[]{moduleId});
        if (modules == null) {
            return false;
        }

        String delModule = "delete from ctp_module";
        String idStr = "(";
        int index = 0;
        for (Map<String, Object> module : modules) {
            index++;
            idStr += "'" + module.get("id") + "'";
            if (index < modules.size()) {
                idStr += ",";
            }
        }
        idStr += ")";

        delModule += " where id in " + idStr;
        if (dm.deleteBySql(delModule, null) < 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean modifyModule(Map<String, Object> module)
            throws BaseException {
//		if (mt != null) {
//			module.put("groupid", mt.getModuleType());
//		}

        if (dm.updateByTableName("ctp_module", module, null) < 1) {
            return false;
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> getModules() throws BaseException {
        String sql = "select id, parentid, modulename, indexnum, name, moduletype, groupid, target from ctp_module x start with x.parentid is null connect by x.parentid = prior x.id order SIBLINGS BY indexnum";
        return dm.queryForList(sql, null);
    }

    @Override
    public List<Map<String, Object>> getModulesByType(ModuleType mt) throws BaseException {
        String sql = "select id, parentid as pid, modulename  as name from ctp_module x where x.groupid=? "
                + "start with x.parentid is null connect by x.parentid = prior x.id order SIBLINGS BY indexnum";
        if (ModuleType.SYSTEM.equals(mt)) {
            sql = "select id, parentid as pid, modulename as name from ctp_module x "
                    + "start with x.parentid is null connect by x.parentid = prior x.id order SIBLINGS BY indexnum";
            return dm.queryForList(sql, null);
        }
        List<Map<String, Object>> data = dm.queryForList(sql, new String[]{mt.getModuleType()});
        return data;
    }

}

