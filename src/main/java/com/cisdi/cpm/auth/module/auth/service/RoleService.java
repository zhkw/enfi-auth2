package com.cisdi.cpm.auth.module.auth.service;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.common.contant.RoleType;
import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;
import java.util.Map;

public interface RoleService {
    /**
     * get roles with role type, if rt is null, get all.
     *
     * @param rt
     * 		role type, includes project��system and administrator
     * @return
     * @throws BaseException
     */
    public Map<String, Object> getRoles(DataQueryBean dq, RoleType rt) throws BaseException;

    /**
     * delete role with roleId
     *
     * @param roleId
     * @return
     * @throws BaseException
     */
    public boolean deleteRole(String roleId) throws BaseException;

    /**
     * add role with role type
     *
     * @param role
     * 		role information
     * @param rt
     * 		role type(Enumeration)
     * @return
     * @throws BaseException
     */
    public boolean addRole(Map<String, Object> role, RoleType rt) throws BaseException;

    /**
     * modify role info
     *
     * @param role
     * 		new role information
     * @param rt
     * 		new role type
     * @return
     * @throws BaseException
     */
    public boolean modifyRole(Map<String, Object> role, RoleType rt) throws BaseException;

    /**
     * get all role type
     *
     * @return
     */
    public List<Map<String, Object>> getRoleType() throws BaseException;

    /**
     * clean all resources of role
     *
     * @param roleId
     * @return
     * @throws BaseException
     */
    public boolean clearAuthOfRole(String roleId) throws BaseException;

    /**
     * 获取用户设置的系统角色
     * @param userId
     * @return
     * @throws BaseException
     */
    public List<String> getSelSysUserRole(String userId) throws BaseException;

    /**
     * 获取用户设置的项目角色
     * @param userId
     * @param projId
     * @return
     * @throws BaseException
     */
    public List<String> getSelProjUserRole(String userId, String projId) throws BaseException ;
}
