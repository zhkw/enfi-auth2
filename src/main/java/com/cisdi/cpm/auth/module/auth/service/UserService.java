package com.cisdi.cpm.auth.module.auth.service;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.exception.BaseException;

import java.util.Map;

public interface UserService {
    /**
     * query user with keyword
     *
     * @param keyword
     * 		if keyword = null, query without keyword
     * @param page
     * 		current page
     * @param pageSize
     * 		page size
     * 	if page = 0 and pageSize = -1, query all.
     * @return
     */
    public Map<String, Object> queryUsers(DataQueryBean dq) throws BaseException;

    /**
     * delete user
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public boolean deleteUser(String userId) throws BaseException;

    /**
     * add new user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public boolean addUser(Map<String, Object> user) throws BaseException;

    /**
     * modify user info
     *
     * @param user
     * 		the userId must be in user
     * @return
     * @throws Exception
     */
    public boolean modifyUser(Map<String, Object> user) throws BaseException;

    /**
     * get participant projects of user
     *
     * @param userId
     * @return
     * @throws BaseException
     */
    public Map<String, Object> getRelatedProjOfUser(String userId, String keyword) throws BaseException;

    /**
     * 关联用户和人员
     *
     * @param userId
     * @param personId
     * @return
     * @throws BaseException
     */
    public boolean bindUserAndPerson(String userId, String personId) throws BaseException;


}

