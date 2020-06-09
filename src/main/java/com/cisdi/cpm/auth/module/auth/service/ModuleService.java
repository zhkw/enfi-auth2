package com.cisdi.cpm.auth.module.auth.service;

import com.cisdi.cpm.auth.common.contant.ModuleType;
import com.cisdi.cpm.auth.exception.BaseException;

import java.util.List;
import java.util.Map;

public interface ModuleService {

    /**
     * add module
     *
     * @param module
     * @return
     * @throws BaseException
     */
    public String addModule(Map<String, Object> module) throws BaseException;

    /**
     * delete module, if this module has children nodes, delete its all children nodes.
     *
     * @param ModuleId
     * @return
     * @throws BaseException
     */
    public boolean deleteModule(String moduleId) throws BaseException;

    /**
     * modify module information, and module must include moduleId
     *
     * @param module
     *		new module information
     * @param mt
     * 		new module type
     * @return
     * @throws BaseException
     */
    public boolean modifyModule(Map<String, Object> module) throws BaseException;

    /**
     * get all modules.
     *
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> getModules() throws BaseException;

    /**
     * get modules by moduleType
     *
     * @return
     * @throws BaseException
     */
    public List<Map<String, Object>> getModulesByType(ModuleType mt) throws BaseException;
}

