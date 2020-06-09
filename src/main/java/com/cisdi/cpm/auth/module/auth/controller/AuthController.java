package com.cisdi.cpm.auth.module.auth.controller;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.common.BaseController;
import com.cisdi.cpm.auth.common.contant.ModuleType;
import com.cisdi.cpm.auth.common.contant.RoleType;
import com.cisdi.cpm.auth.module.auth.entity.RoleEntity;
import com.cisdi.cpm.auth.module.auth.service.AuthService;
import com.cisdi.cpm.auth.module.auth.service.ModuleService;
import com.cisdi.cpm.auth.module.auth.service.RoleService;
import com.cisdi.cpm.auth.utils.PageUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("authMng")
public class AuthController extends BaseController {

    @Resource(name="authRoleService")
    RoleService rs;

    @Resource(name="authModuleService")
    ModuleService ms;

    @Resource(name="authAuthService")
    AuthService as;

    /**
     * 初始化权限管理界面
     * @return
     */
    @RequestMapping("init")
    public ModelAndView authMngInit(){
        ModelAndView mv=createLayoutView("auth/authMng.ftl");
        return mv;
    }

    /**
     * 查询角色列表
     * @param keyword
     * @param pageSize
     * @param cuttentPage
     * @param type M:多项目 S:单项目
     * @return
     * @throws Exception
     */
    @RequestMapping("queryList")
    @ResponseBody
    public Map<String,Object> queryRoles(String keyword, int pageSize, int currentPage, @RequestParam String type) {
        RoleType rt = RoleType.SYSTEM;
        if("S".equals(type)){
            rt = RoleType.PROJECT;
        }

        DataQueryBean dq = new DataQueryBean();
        dq.setKeyword(keyword);
        dq.setPage(currentPage);
        dq.setPageSize(pageSize);
        Map<String, Object> map = rs.getRoles(dq, rt);

        map.put("currentPage", currentPage);//当前页
        map.put("pagesNum", PageUtils.getTotalPage((long) map.get("PAGE_TOTALCOUNT"), pageSize));//共多少页

        return map;
    }

    /**
     * 对角色进行CUD操作
     * @param oper
     * @param role
     * @param type M:多项目 S:单项目
     * @return
     * @
     */
    @RequestMapping("handleRole")
    @ResponseBody
    public boolean handelRoles(@RequestParam String oper, RoleEntity role, @RequestParam String type) {
        Map<String, Object> roleMap = new HashMap<String, Object>();
        RoleType rt = RoleType.SYSTEM;
        if("S".equals(type)){
            rt = RoleType.PROJECT;
        }

        if("update".equals(oper)){
            if (role.getId() == null) {
                return false;
            }
            roleMap.put("id", role.getId());
            roleMap.put("description", role.getDescription());
            roleMap.put("name", role.getRolename());
            roleMap.put("updateTime", new Date());
            return rs.modifyRole(roleMap, rt);
        }else if("add".equals(oper)){
            roleMap.put("name", role.getRolename());
            roleMap.put("description", role.getDescription());
            roleMap.put("createTime", new Date());
            roleMap.put("updateTime", new Date());
            return rs.addRole(roleMap, rt);
        }else{
            return rs.deleteRole(role.getId());
        }
    }

    /**
     * 查询服务模块树形结构
     * @param keyword
     * @param roleId
     * @param type M:多项目 S:单项目
     * @return
     * @
     */
    @RequestMapping("querySM")
    @ResponseBody
    public List<Map<String, Object>> queryServiceModules(String keyword, String roleId, String type) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ModuleType mt = ModuleType.SYSTEM;
        if("S".equals(type)){
            mt = ModuleType.PROJECT;
        }
        //获取roleid对应的资源列表
        List<String> rids = as.getSelResourcesOfRole(roleId);
        list = ms.getModulesByType(mt);
        if (rids != null) {
            for (Map<String, Object> resource : list) {
                for (String rid : rids) {
                    if (resource.get("id").equals(rid)) {
                        resource.put("checked", true);
                        break;
                    }
                }
            }
        }

        return list;
    }

    /**
     * 角色模块分配
     * @param roleId
     * @param moudleIds
     * @param oper
     * @param type
     * @return
     * @
     */
    @RequestMapping("allocatSM")
    @ResponseBody
    public boolean handleAllocatSM(@RequestParam String roleId,String moudleIds,@RequestParam String oper,@RequestParam String type) {
        if("M".equals(type)){
            if("reset".equals(oper)){
                return rs.clearAuthOfRole(roleId);
            }
            if("update".equals(oper)){
                String[] smIds=moudleIds.split(",");//模块ID
                return as.setRoleResources(roleId, Arrays.asList(smIds));
            }
        }else{
            if("reset".equals(oper)){
                rs.clearAuthOfRole(roleId);
            }
            if("update".equals(oper)){
                String[] smIds=moudleIds.split(",");//模块ID
                return as.setRoleResources(roleId, Arrays.asList(smIds));
            }
        }
        return true;
    }

}

