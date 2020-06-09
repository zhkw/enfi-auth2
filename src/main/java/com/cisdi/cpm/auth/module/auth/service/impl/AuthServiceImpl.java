package com.cisdi.cpm.auth.module.auth.service.impl;

import com.cisdi.cpm.auth.common.contant.DataStatus;
import com.cisdi.cpm.auth.datamng.CommonService;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.AbortException;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.module.auth.service.AuthService;
import com.cisdi.cpm.auth.utils.StrUtils;
import com.cisdi.cpm.auth.utils.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("authAuthService")
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Resource
    DataMng dm;

    @Resource(name="authCommonService")
    CommonService cs;

    @Override
    public boolean IsPersonExistsInTeamMember(String projId, String personId) throws BaseException {
        String sql = "select count(1) from proj_team_member where fk_proj_id=? and fk_person_id=?";
        if (dm.getCount(sql, new String[]{projId, personId}) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean addPersonToTeamMember(String projId, String userId)
            throws BaseException {
        String personId = getPersonIdByUserId(userId);
        if (personId == null) {
            throw new AbortException("该人员不存在，添加失败");
        }

        if (IsPersonExistsInTeamMember(projId, personId)) {
            throw new AbortException("该用户已经存在于该小组成员中");
        }

        //Get person information
        Map<String, String> sqls = build_SQL_teammember();

        List<Map<String, Object>> person = dm.queryForList(sqls.get("sql_person"), new String[]{personId});
        List<Map<String, Object>> project = dm.queryForList(sqls.get("sql_project"), new String[]{projId});

        if (person == null || person.size() == 0 || project == null || project.size() == 0) {
            return false;
        }

        Map<String, Object> teamMember = person.get(0);
        teamMember.putAll(project.get(0));




        if (dm.insertByTableName("proj_team_member", teamMember) == null) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteUserFromTeamMember(String projId, String userId)
            throws BaseException {
        String personId = getPersonIdByUserId(userId);
        //从项目小组成员中删除
        String sql = "delete from proj_team_member where fk_proj_id=? and fk_person_id=?";
        //删除该用户对应在项目中的角色信息
        String sql_role = "delete from auth_rel_proj_role where proj_id=? and user_id=?";

        dm.deleteBySql(sql, new String[]{projId, personId});
        dm.deleteBySql(sql_role, new String[]{projId, userId});

        return true;
    }

    @Override
    @Transactional
    public boolean setRoleResources(String roleId, List<String> resources)
            throws BaseException {
        //delete old resources of roleid
        String delOldAuth = "delete from ctp_auth_rule where roleId=?";
        dm.deleteBySql(delOldAuth, new String[]{roleId});

        //batch add role auth
        String batchAuth = "insert into ctp_auth_rule(id, createTime, updateTime, roleId, resourceId) values(?,?,?,?,?)";
        List<Object[]> batchParams = new ArrayList<Object[]>();
        for (String resource : resources) {
            Object[] param = new Object[5];
            param[0] = StrUtils.getUUID();
            param[1] = new Date();
            param[2] = new Date();
            param[3] = roleId;
            param[4] = resource;

            batchParams.add(param);
        }
        dm.batchUpdate(batchAuth, batchParams);

        return true;
    }

    @Override
    @Transactional
    public boolean setSysUserRole(List<String> users, List<String> roles)
            throws BaseException {
        if (users == null || users.size() == 0) {
            return false;
        }

		/*	batch delete users' roles, if users' size is greater than 1, each user's roles of users is the same.
			so, before setting user role, each user's role of roles should be empty
		*/
        String batchDel = "delete from ctp_rel_user_role";
        String batchDelCondition = "(";
        int userIndex = 0;
        for (String userId : users) {
            userIndex++;
            batchDelCondition += "'" + userId + "'";
            if (userIndex < users.size()) {
                batchDelCondition += ",";
            }
        }
        batchDelCondition += ")";
        dm.deleteBySql((batchDel + " where leftid in " + batchDelCondition), null);

        //reset user role
        if (roles == null || roles.size() == 0) {
            return false;
        }
        String batchAdd = "insert into ctp_rel_user_role(id, createTime, updateTime, leftclassid, leftid, rightclassid, rightid) values (?,?,?,?,?,?,?)";
        List<Object[]> batchAddParams = new ArrayList<Object[]>();
        for (String userId : users) {
            for (String roleId : roles) {
                Object[] param = new Object[7];
                param[0] = StrUtils.getUUID();
                param[1] = new Date();
                param[2] = new Date();
                param[3] = "user";
                param[4] = userId;
                param[5] = "role";
                param[6] = roleId;
                batchAddParams.add(param);
            }
        }

        dm.batchUpdate(batchAdd, batchAddParams);
        return true;
    }

    @Override
    @Transactional
    public boolean setProjUserRole(String projId, List<String> users,
                                   List<String> roles) throws BaseException {
        if (users == null || users.size() == 0) {
            return false;
        }

        //batch delete user-role of project
        String batchDel = "delete from auth_rel_proj_role where proj_id=? and user_id=?";
        List<Object[]> batchDelParams = new ArrayList<Object[]>();
        for (String userId : users) {
            Object[] param = new Object[2];
            param[0] = projId;
            param[1] = userId;
            batchDelParams.add(param);
        }
        dm.batchUpdate(batchDel, batchDelParams);

        //reset project-user-role
        if (roles == null || roles.size() == 0) {
            return false;
        }

        String batchAdd = "insert into auth_rel_proj_role(id, createTime, updateTime, proj_id, role_id, user_id, data_status) values(?,?,?,?,?,?,?)";
        List<Object[]> batchAddParams = new ArrayList<Object[]>();
        for (String userId : users) {
            for (String roleId : roles) {
                Object[] param = new Object[7];
                param[0] = StrUtils.getUUID();
                param[1] = new Date();
                param[2] = new Date();
                param[3] = projId;
                param[4] = roleId;
                param[5] = userId;
                param[6] = DataStatus.ADD.getDataStatus();
                batchAddParams.add(param);
            }
        }
        dm.batchUpdate(batchAdd, batchAddParams);

        return true;
    }

    @Override
    public List<String> getSelResourcesOfRole(String roleId) throws BaseException {
        String sql = "select resourceid from ctp_auth_rule where roleid=?";
        List<Map<String, Object>> resources = dm.queryForList(sql, new String[]{roleId});
        if (resources == null) {
            return null;
        }
        List<String> rids = new ArrayList<String>();
        for (Map<String, Object> resource : resources) {
            rids.add((String) resource.get("resourceid"));
        }
        return rids;
    }

    /**
     * 通过用户ID获取人员ID
     *
     * @param userId
     * @return
     * @throws BaseException
     */
    private String getPersonIdByUserId(String userId) throws BaseException{
        String sql = "select fk_person_id from rel_user_person where fk_user_id=?";

        Map<String, String> relUserPersonTable = TableUtils.getTableFromCache("rel_user_person");
        boolean isRebuildSql = false;
        if ((relUserPersonTable != null && "Y".equals(relUserPersonTable.get("isModified")))) {
            isRebuildSql = true;
        }
        if (!isRebuildSql) {
            sql = "select "
                    +  relUserPersonTable.get("fk_person_id")
                    + " from "
                    + relUserPersonTable.get("tableName")
                    + " where "
                    + relUserPersonTable.get("fk_user_id")
                    + "=?";
        }

        List<Map<String, Object>> person = dm.queryForList(sql, new String[]{userId});
        if (person != null && person.size() > 0) {
            if (isRebuildSql) {
                return (String) person.get(0).get(relUserPersonTable.get("fk_person_id"));
            } else {
                return (String) person.get(0).get("fk_person_id");
            }

        }

        return null;
    }

    private Map<String, String> build_SQL_teammember() {
        //Get person information
        String sql_person = "select id as fk_person_id, email as email_address, name as person_name, employee_num as person_num "
                + "from person"
                + "where id=?";

        String sql_project = "select id as fk_proj_id, proj_name, proj_num, proj_setup_date as start_date from project where id=?";

        Map<String, String> personColumn = TableUtils.getTableFromCache("person");
        boolean isRebuildSql_person = false;
        if ((personColumn != null && "Y".equals(personColumn.get("isModified")))) {
            isRebuildSql_person = true;
        }

        Map<String, String> projectColumn = TableUtils.getTableFromCache("project");
        boolean isRebuildSql_project = false;
        if ((projectColumn != null && "Y".equals(projectColumn.get("isModified")))) {
            isRebuildSql_project = true;
        }

        if (isRebuildSql_person) {
            TableUtils.buildTableColumn(personColumn, null);
            sql_person = "select "
                    + personColumn.get("id").substring(1)
                    + " as fk_person_id"
                    + personColumn.get("name")
                    + " as person_name"
                    + personColumn.get("employee_num")
                    + " as person_num "
                    + " from "
                    + personColumn.get("tableName")
                    + " where "
                    + personColumn.get("id").substring(1)
                    + "=?";
        }

        if (isRebuildSql_project) {
            TableUtils.buildTableColumn(projectColumn, null);
            sql_project = "select "
                    +  projectColumn.get("id").substring(1)
                    + " as fk_proj_id"
                    + projectColumn.get("proj_name")
                    + " as proj_name "
                    + projectColumn.get("proj_num")
                    + " as proj_num from "
                    + projectColumn.get("tableName")
                    + " where "
                    + projectColumn.get("id").substring(1)
                    + "=?";
        }

        Map<String, String> result = new HashMap<String,String>();
        result.put("sql_person", sql_person);
        result.put("sql_project", sql_project);

        return result;
    }

}

