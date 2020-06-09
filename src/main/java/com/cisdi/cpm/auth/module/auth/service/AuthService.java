package com.cisdi.cpm.auth.module.auth.service;

import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;

public interface AuthService {

    /**
     * judge whether user exists in some project team-member
     *
     * @param projId
     * 		project ID
     * @param personId
     * 		person ID
     * @return
     * @throws BaseException
     */
    public boolean IsPersonExistsInTeamMember(String projId, String personId) throws BaseException;

    /**
     * add user to team-member
     *
     * @param projId
     * 		project ID
     * @param userId
     * 		�û� ID
     * @throws BaseException
     */
    public boolean addPersonToTeamMember(String projId, String personId) throws BaseException;

    /**
     * delete user from team-member
     *
     * @param projId
     * @param userId
     * @return
     * @throws BaseException
     */
    public boolean deleteUserFromTeamMember(String projId, String userId) throws BaseException;

    /**
     * allocate resource to role.
     *
     * @param roleId
     * 		role ID
     * @param Resources
     * 		resources ID list
     * @return
     * @throws BaseException
     */
    public boolean setRoleResources(String roleId, List<String> Resources) throws BaseException;

    /**
     * allocate system role to user
     *
     * @param users
     * 		user ID list
     * @param roles
     * 		role ID list
     * @return
     * @throws BaseException
     */
    public boolean setSysUserRole(List<String> users, List<String> roles) throws BaseException;

    /**
     * allocation project role to user
     *
     * @param projId
     * 		project ID
     * @param users
     * 		user ID list
     * @param roles
     * 		role ID list
     * @return
     * @throws BaseException
     */
    public boolean setProjUserRole(String projId, List<String> users, List<String> roles) throws BaseException;

    /**
     * get allocated resources of role
     *
     * @param roleId
     * @return
     * @throws BaseException
     */
    public List<String> getSelResourcesOfRole(String roleId) throws BaseException;
}

