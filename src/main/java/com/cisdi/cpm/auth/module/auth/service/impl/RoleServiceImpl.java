package com.cisdi.cpm.auth.module.auth.service.impl;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.common.contant.Const;
import com.cisdi.cpm.auth.common.contant.DataStatus;
import com.cisdi.cpm.auth.common.contant.RoleType;
import com.cisdi.cpm.auth.datamng.CommonService;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.AbortException;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.module.auth.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("authRoleService")
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Resource
    DataMng dm;

    @Resource(name="authCommonService")
    CommonService cs;

    @Override
    public Map<String, Object> getRoles(DataQueryBean dq, RoleType rt) throws BaseException {
        String sqlData = "select o.id, o.name, o.description, o.createtime, o.updatetime from ctp_role o "
                + "join auth_rel_role_type r on o.id = r.role_id ";
        String sqlCount = "select count(1) from ctp_role o "
                + "join auth_rel_role_type r on o.id = r.role_id ";

        String[] params = {rt.getRoleType()};
        List<Map<String, Object>> data = cs.searchBySql(sqlData, new String[]{"o.name"},
                dq.getKeyword(), buildRoleTypeCondition(rt, "r"), params, dq.getPage(), dq.getPageSize(), "order by o.createTime desc");
        long totalCount = cs.getSearchCount(sqlCount, new String[]{"o.name"}, dq.getKeyword(), buildRoleTypeCondition(rt, "r"), params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Const.PAGE_DATA, data);
        result.put(Const.PAGE_TOTALCOUNT, totalCount);

        return result;
    }

    @Override
    @Transactional
    public boolean deleteRole(String roleId) throws BaseException {
        String sql_del_role = "delete from ctp_role where id=?";
        String sql_rel = "delete from auth_rel_role_type where role_id =?";
        String[] params = new String[]{roleId};

        int delRoleNum = dm.deleteBySql(sql_del_role, params);
        int delRelNum = dm.deleteBySql(sql_rel, params);
        if (delRoleNum == 0 || delRelNum == 0) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean addRole(Map<String, Object> role, RoleType rt)
            throws BaseException {
        if (roleIsExists((String) role.get("id"), (String) role.get("name"), rt.getRoleType())) {
            throw new AbortException("该角色已经存在");
        }
        String roleId = dm.insertByTableName("ctp_role", role);
        if (roleId == null) {
            return false;
        }
        Map<String, Object> rel = new HashMap<String, Object>();
        rel.put("role_id", roleId);
        rel.put("role_type_flag", rt.getRoleType());
        rel.put("data_status", DataStatus.ADD.getDataStatus());
        rel.put("createTime", new Date());
        rel.put("updateTime", new Date());
        String relId = dm.insertByTableName("auth_rel_role_type", rel);
        if (relId == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean modifyRole(Map<String, Object> role, RoleType rt)
            throws BaseException {
        if (roleIsExists((String) role.get("id"), (String) role.get("name"), rt.getRoleType())) {
            throw new AbortException("该角色已经存在");
        }
        int updateIndex = dm.updateByTableName("ctp_role", role, null);
        if (updateIndex < 1) {
            return false;
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> getRoleType() throws BaseException {
        String sql = "select id, role_code, role_name from auth_role_type";
        return dm.queryForList(sql, null);
    }

    /**
     * 获取已经配置的用户角色列表
     */
    @Override
    public List<String> getSelSysUserRole(String userId) throws BaseException {
        String sql = "select rightid from ctp_rel_user_role where leftid=?";
        List<Map<String, Object>> data = dm.queryForList(sql, new Object[]{userId});
        List<String> roleIds = new ArrayList<String>();
        if (data != null) {
            for (Map<String, Object> role : data) {
                roleIds.add((String) role.get("rightid"));
            }
        }

        return roleIds;
    }

    @Override
    public List<String> getSelProjUserRole(String userId, String projId) throws BaseException {
        String sql = "select role_id from auth_rel_proj_role where proj_id=? and user_id=?";
        List<Map<String, Object>> data = dm.queryForList(sql, new Object[]{projId, userId});
        List<String> roleIds = new ArrayList<String>();
        if (data != null) {
            for (Map<String, Object> role : data) {
                roleIds.add(role.get("role_id").toString());
            }
        }

        return roleIds;
    }


    private String buildRoleTypeCondition(RoleType rt, String mark) {
        StringBuffer condition = new StringBuffer("");
//		if (rt == null) {
//			condition.append(" 1=1 ");
//			return condition.toString();
//		}
        condition.append(mark);
        condition.append(".role_type_flag=?");

        return condition.toString();
    }

    /**
     * 验证角色是否存在
     */
    private boolean roleIsExists(String id, String roleName, String rt) throws BaseException {
        String condition = "";
        if (id == null) {
            condition = "c.id is not null";
        } else {
            condition = "c.id<>'" + id + "'";
        }
        String sql = "select count(1) from ctp_role c join auth_rel_role_type r on c.id = r.role_id where c.name=? and r.role_type_flag=? and " + condition;
        long count = dm.getCount(sql, new Object[]{roleName, rt});
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean clearAuthOfRole(String roleId) throws BaseException {
        String sql = "delete from ctp_auth_rule where roleid=?";
        dm.deleteBySql(sql, new Object[]{roleId});

        return true;
    }

}

