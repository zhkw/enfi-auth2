package com.cisdi.cpm.auth.module.auth.controller;

import com.cisdi.cpm.auth.bean.DataQueryBean;
import com.cisdi.cpm.auth.common.BaseController;
import com.cisdi.cpm.auth.common.contant.Const;
import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.module.auth.service.UserService;
import com.cisdi.cpm.auth.utils.PageUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource(name="authUserService")
    UserService us;

    @RequestMapping("")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = createLayoutView("user/usermng.ftl");
        return mav;
    }

    @RequestMapping("handle")
    @ResponseBody
    public Map<String,Object> handleUser(@RequestBody Map<String, Object> user) throws BaseException{
        Map<String, Object> result = new HashMap<String, Object>();
        String oper=(String)user.get("oper");
        user.remove("oper");
        Boolean update=false;
        if("add".equals(oper)){
            if (us.addUser(user)) {
                result.put("status", "Y");
            } else {
                result.put("status", "N");
            }
        }else{
            update=us.modifyUser(user);
            if(update){
                result.put("status", "Y");
            }else{
                result.put("status", "N");
            }
        }
        return result;
    }

    @RequestMapping("deleteUser")
    @ResponseBody
    public Map deleteUser(@RequestBody Map<String, Object> delUser) throws BaseException{
        String userId = (String) delUser.get("userId");
        Map<String, Object> result = new HashMap<String, Object>();
        if (userId == null) {
            result.put("isOk", "N");
            return result;
        }
        if (us.deleteUser(userId)) {
            result.put("isOk", "Y");
        } else {
            result.put("isOk", "N");
        }

        return result;
    }
    //
    @RequestMapping("projectOfUser")
    @ResponseBody
    public Map<String, Object> getProjectOfUser(String userId, String keyword) throws BaseException {

        Map<String, Object> result = us.getRelatedProjOfUser(userId, keyword);
        return result;
    }

    @RequestMapping("nextPage")
    @ResponseBody
    public Map<String, Object> getNextPage(@RequestParam("keyword") String keyword, @RequestParam("nextPage") int nextPage, int pageSize) throws BaseException{
        DataQueryBean dq = new DataQueryBean();
        dq.setKeyword(keyword);
        dq.setPage(nextPage);
        dq.setPageSize(pageSize);

        Map<String, Object> result = us.queryUsers(dq);
        result.put(Const.CURRENT_PAGE, nextPage);
        result.put(Const.TOTAL_PAGE, PageUtils.getTotalPage((long) result.get(Const.PAGE_TOTALCOUNT),pageSize));
        result.put(Const.PAGESIZE,pageSize);
        return result;
    }

}

