package com.cisdi.cpm.auth.module.auth.service.impl;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.common.contant.Const;
import com.cisdi.cpm.auth.datamng.CommonService;
import com.cisdi.cpm.auth.datamng.DataMng;
import com.cisdi.cpm.auth.exception.AbortException;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.module.auth.service.UserService;
import com.cisdi.cpm.auth.utils.TableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("authUserService")
public class UserServiceImpl implements UserService {

    @Resource(name="authCommonService")
    CommonService cs;

    @Resource
    DataMng dm;

    @Override
    public Map<String, Object> queryUsers(DataQueryBean dq) throws BaseException {
        Map<String, Object> sqlResult = build_SQL_QueryUser();

        String sqlData = (String) sqlResult.get("sqlData");
        String sqlCount = (String) sqlResult.get("sqlCount");
        String[] attrs = (String[]) sqlResult.get("attrs");

        List<Map<String, Object>> data = cs.searchBySql(sqlData, attrs, dq.getKeyword(), null, null,
                dq.getPage(), dq.getPageSize(), "order by u.createTime desc");
        long totalCount = cs.getSearchCount(sqlCount, attrs, dq.getKeyword(), null, null);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Const.PAGE_DATA, data);
        result.put(Const.PAGE_TOTALCOUNT, totalCount);

        Map<String, String> personColumnMap = TableUtils.getTableFromCache("person");
        if ("Y".equals(personColumnMap.get("isModified"))) {
            result.put("personColumnMap", personColumnMap);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean deleteUser(String userId) throws BaseException {
        //处理SQL语句
        Map<String, String> result = build_SQL_deleteUser();
        String sql_del_person = result.get("sql_del_person");
        String sql_del_rel  = result.get("sql_del_rel");
        String sql_ctp_user = "delete from ctp_user where id=?";
        String[] params = {userId};
        int index_del_user = dm.deleteBySql(sql_ctp_user, params);
        int index_del_person = dm.deleteBySql(sql_del_person, params);
        int index_del_rel = dm.deleteBySql(sql_del_rel, params);
        if (index_del_user == 0 || index_del_person == 0 || index_del_rel == 0) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addUser(Map<String, Object> user) throws BaseException {
        checkUser(null, (String) user.get("userName"));
        //CTP_USER
        Map<String, Object> ctpUser = new HashMap<String, Object>();
        ctpUser.put("name", user.get("userName"));
        ctpUser.put("password", user.get("password"));
        ctpUser.put("gender", user.get("gender"));
        ctpUser.put("createtime", new Date());
        ctpUser.put("updatetime", new Date());
        String userId = dm.insertByTableName("ctp_user", ctpUser);
        if (userId == null) {
            return false;
        }

        //PERSON
        Map<String, Object> personTable = this.build_SQL_addPerson(user);
        String personId = dm.insertByTableName((String)personTable.get("tableName"), (Map<String, Object>) personTable.get("person"));
        if (personId == null) {
            return false;
        }

        //REL_USER_PERSON
        Map<String, Object> relUserPersonTable = this.build_SQL_addRelUserPerson(userId, personId);
        String rel_user_person_id = dm.insertByTableName((String) relUserPersonTable.get("tableName"), (Map<String, Object>) relUserPersonTable.get("rel_user_person"));
        if (rel_user_person_id == null) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean modifyUser(Map<String, Object> user) throws BaseException {
        String userId = (String) user.get("userId");
        checkUser(userId, (String) user.get("userName"));
        user.remove("userId");
        if (userId == null) {
            return false;
        }
        String sql = build_SQL_modifyUser();
        List<Map<String, Object>> person = dm.queryForList(sql, new String[]{userId});
        if (person == null || person.size() == 0) {
            return false;
        }
        String personId = (String) person.get(0).get("fk_person_id");

        Map<String, Object> newUser = new HashMap<String, Object>();
        if (user.containsKey("userName")) {
            newUser.put("name", user.get("userName"));
            user.remove("userName");
        }
        if (user.containsKey("password")) {
            newUser.put("password", user.get("password"));
            user.remove("password");
        }
        if (user.containsKey("gender")) {
            newUser.put("gender", user.get("gender"));
            user.remove("gender");
        }
        newUser.put("id", userId);
        dm.updateByTableName("ctp_user", newUser, null);

        user.put("id", personId);
        Map<String, Object> person_modify = this.build_SQL_addPerson(user);
        dm.updateByTableName((String) person_modify.get("tableName"), (Map<String, Object>) person_modify.get("person"), null);

        return true;
    }

    @Override
    public Map<String, Object> getRelatedProjOfUser(String userId, String keyword)
            throws BaseException {
        Map<String, Object> result = build_SQL_project();
        String sql = (String) result.get("sql");
        String[] attrs = (String[]) result.get("attrs");

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> projects = cs.searchBySql(sql, attrs, keyword, null, new String[]{userId}, 0, -1, null);
        Map<String, String> projectColumnMap = TableUtils.getTableFromCache("project");

        data.put("data", projects);
        data.put("projectColumnMap", projectColumnMap);

        return data;
    }

    private boolean checkUser(String userId, String userName) throws BaseException  {
        String condition = " 1=1 ";
        if (userId == null) {
            condition = "id is not null";
        } else {
            condition = "id <> '" + userId + "'";
        }

        String sql="SELECT COUNT(*) FROM CTP_USER WHERE NAME=? and " + condition;
        String[] params={userName};
        long count=dm.getCount(sql, params);
        if(count>0){
            throw new AbortException("该用户已存在");
        }else{
            return true;
        }
    }

    @Override
    public boolean bindUserAndPerson(String userId, String personId)
            throws BaseException {
        // TODO Auto-generated method stub
        return false;
    }


    //----------build SQL----------------//
    private Map<String, Object> build_SQL_QueryUser() {
        String sqlData
                = "select u.id, u.name as username, u.gender, p.name, p.employee_num, p.email, p.phone, p.specialty "
                + "from ctp_user u "
                + "join rel_user_person r on u.id = r.fk_user_id "
                + "join person p on r.fk_person_id = p.id";

        String sqlCount
                = "select count(1) from ctp_user u "
                + "join rel_user_person r on u.id = r.fk_user_id "
                + "join person p on r.fk_person_id = p.id";

        String[] attrs = new String[]{"u.name", "p.name", "p.employee_num"};

        Map<String, String> personTable = TableUtils.getTableFromCache("person");
        Map<String, String> relUserPersonTable = TableUtils.getTableFromCache("rel_user_person");
        boolean isRebuildSql = false;
        if ((personTable != null && "Y".equals(personTable.get("isModified")))
                || (relUserPersonTable != null && "Y".equals(relUserPersonTable.get("isModified")))) {
            isRebuildSql = true;
        }
        //需要对SQL进行重新构造
        if (isRebuildSql) {
            //添加表别名
            TableUtils.buildTableColumn(personTable, "p");
            TableUtils.buildTableColumn(relUserPersonTable, "r");

            sqlData = "select u.id, u.name as username, u.gender"
                    + personTable.get("name")
                    + personTable.get("employee_num")
                    + personTable.get("email")
                    + personTable.get("phone")
                    + personTable.get("specialty")
                    + " from ctp_user u"
                    + " join "
                    + relUserPersonTable.get("tableName")
                    + " r on u.id="
                    + relUserPersonTable.get("fk_user_id").substring(1)
                    + " join "
                    + personTable.get("tableName")
                    + " p on "
                    + relUserPersonTable.get("fk_person_id").substring(1)
                    + "="
                    + personTable.get("id").substring(1);

            sqlCount = "select count(1) from ctp_user u"
                    + " join "
                    + relUserPersonTable.get("tableName")
                    + " r on u.id="
                    + relUserPersonTable.get("fk_user_id").substring(1)
                    + " join "
                    + personTable.get("tableName")
                    + " p on "
                    + personTable.get("id").substring(1)
                    + "="
                    + relUserPersonTable.get("fk_person_id").substring(1);

            attrs[0] = "u.name";
            attrs[1] = personTable.get("name").substring(1);
            attrs[2] = personTable.get("employee_num").substring(1);

        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sqlData", sqlData);
        result.put("sqlCount", sqlCount);
        result.put("attrs", attrs);

        return result;
    }

    private Map<String, String> build_SQL_deleteUser() {
        String sql_del_person = "delete from person where id = (select fk_person_id from rel_user_person where fk_user_id=?)";
        String sql_del_rel = "delete from rel_user_person where fk_user_id=?";

        Map<String, String> personTable = TableUtils.getTableFromCache("person");
        Map<String, String> relUserPersonTable = TableUtils.getTableFromCache("rel_user_person");
        boolean isRebuildSql = false;
        if ((personTable != null && "Y".equals(personTable.get("isModified")))
                || (relUserPersonTable != null && "Y".equals(relUserPersonTable.get("isModified")))) {
            isRebuildSql = true;
        }

        if (isRebuildSql) {
            sql_del_person
                    = "delete from "
                    + personTable.get("tableName")
                    + " where id = "
                    + "(select "
                    + relUserPersonTable.get("fk_person_id")
                    + " from "
                    + relUserPersonTable.get("tableName")
                    + " where "
                    + relUserPersonTable.get("fk_user_id")
                    + "=?)";

            sql_del_rel
                    = "delete from "
                    + relUserPersonTable.get("tableName")
                    + " where "
                    + relUserPersonTable.get("fk_user_id")
                    + "=?";
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("sql_del_person", sql_del_person);
        result.put("sql_del_rel", sql_del_rel);
        return result;
    }

    private Map<String, Object> build_SQL_addPerson(Map<String, Object> user) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> person = new HashMap<String, Object>();
        result.put("tableName", "person");

        person.put("name", user.get("name"));
        person.put("employee_num", user.get("employee_num"));
        person.put("phone", user.get("phone"));
        person.put("email", user.get("email"));
        person.put("specialty", user.get("specialty"));
        person.put("createtime", new Date());
        person.put("updatetime", new Date());

        //PERSON
        Map<String, String> personTable = TableUtils.getTableFromCache("person");
        boolean isRebuildSql = false;
        if ((personTable != null && "Y".equals(personTable.get("isModified")))) {
            isRebuildSql = true;
        }

        if (isRebuildSql) {
            person = new HashMap<String, Object>();
            if (personTable.get("name") != null || !"".equals(personTable.get("name").trim())) {
                person.put(personTable.get("name"), user.get("name"));
            }
            if (personTable.get("employee_num") != null || !"".equals(personTable.get("employee_num").trim())) {
                person.put(personTable.get("employee_num"), user.get("employee_num"));
            }
            if (personTable.get("phone") != null || !"".equals(personTable.get("phone").trim())) {
                person.put(personTable.get("phone"), user.get("phone"));
            }
            if (personTable.get("email") != null || !"".equals(personTable.get("email").trim())) {
                person.put(personTable.get("email"), user.get("email"));
            }
            if (personTable.get("specialty") != null || !"".equals(personTable.get("specialty").trim())) {
                person.put(personTable.get("specialty"), user.get("specialty"));
            }
            person.put("createtime", new Date());
            person.put("updatetime", new Date());

            result.put("tableName", personTable.get("tableName"));
        }

        if (user.containsKey("id")) {
            person.put("id", user.get("id"));
        }

        result.put("person", person);

        return result;
    }

    private Map<String, Object> build_SQL_addRelUserPerson(String userId, String personId) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("tableName", "rel_user_person");

        Map<String, Object> rel_user_person = new HashMap<String, Object>();
        rel_user_person.put("fk_user_id", userId);
        rel_user_person.put("fk_person_id", personId);

        Map<String, String> relUserPersonTable = TableUtils.getTableFromCache("rel_user_person");
        boolean isRebuildSql = false;
        if ((relUserPersonTable != null && "Y".equals(relUserPersonTable.get("isModified")))) {
            isRebuildSql = true;
        }
        if (isRebuildSql) {
            rel_user_person = new HashMap<String, Object>();
            rel_user_person.put(relUserPersonTable.get("fk_user_id"), userId);
            rel_user_person.put(relUserPersonTable.get("fk_person_id"), personId);
            result.put("tableName", relUserPersonTable.get("tableName"));
        }

        result.put("rel_user_person", rel_user_person);
        return result;
    }

    private String build_SQL_modifyUser() {
        String sql = "select r.fk_person_id from ctp_user u join rel_user_person r on u.id = r.fk_user_id where u.id=?";

        Map<String, String> relUserPersonTable = TableUtils.getTableFromCache("rel_user_person");
        boolean isRebuildSql = false;
        if ((relUserPersonTable != null && "Y".equals(relUserPersonTable.get("isModified")))) {
            isRebuildSql = true;
        }

        if (isRebuildSql) {
            TableUtils.buildTableColumn(relUserPersonTable, "r");
            sql = "select "
                    + relUserPersonTable.get("fk_person_id").substring(1)
                    + " from ctp_user u join "
                    + relUserPersonTable.get("tableName")
                    + " r on u.id = "
                    + relUserPersonTable.get("fk_user_id").substring(1)
                    + " where u.id=?";
        }

        return sql;
    }

    private Map<String, Object> build_SQL_project() {
        String sql = "select distinct p.id, p.proj_name, p.proj_num from (select proj_id from auth_rel_proj_role where user_id = ?) r "
                + "join project p on r.proj_id = p.id";

        String[] attrs = new String[]{"p.proj_name", "p.proj_num"};

        Map<String, String> project = TableUtils.getTableFromCache("project");
        boolean isRebuildSql = false;
        if ((project != null && "Y".equals(project.get("isModified")))) {
            isRebuildSql = true;
        }

        if (isRebuildSql) {
            TableUtils.buildTableColumn(project, "p");
            sql = "select distinct "
                    + project.get("id").substring(1)
                    + project.get("proj_name")
                    + project.get("proj_num")
                    + " from (select proj_id from auth_rel_proj_role where user_id = ?) r "
                    + " join "
                    + project.get("tableName")
                    + " p on r.proj_id = "
                    + project.get("id").substring(1);

            attrs[0] = project.get("proj_name").substring(1);
            attrs[1] = project.get("proj_num").substring(1);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sql", sql);
        result.put("attrs", attrs);

        return result;

    }

}
