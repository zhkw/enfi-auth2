package com.cisdi.cpm.auth.module.auth.controller;

import com.cisdi.cpm.auth.common.BaseController;
import com.cisdi.cpm.auth.common.ContextPropHolder;
import com.cisdi.cpm.auth.common.contant.Const;
import com.cisdi.cpm.auth.common.data.SessionInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller

public class IndexController extends BaseController {

    @RequestMapping("")
    public String index() {
        return "redirect:/loginPage";
    }

    @RequestMapping(value = "/loginPage",method = RequestMethod.GET)
    public ModelAndView loginPage() {
        ModelAndView view = new ModelAndView();
        return new ModelAndView("/login");
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        ModelAndView view = new ModelAndView();
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        if (ContextPropHolder.getContextProperty("sys_userName").equals(userName)&&
                ContextPropHolder.getContextProperty("sys_password").equals(password)) {
            view.setViewName("redirect:/user");
            request.getSession().setAttribute(Const.USER_NAME,userName);
            request.getSession().setAttribute(Const.USER_ID,password);
            SessionInfo.set(Const.USER_ID, password);
            SessionInfo.set(Const.USER_NAME, userName);
            return view;
        }else {
            view.setViewName("redirect:/loginPage");
            return view;
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute(Const.USER_ID);
        request.getSession().removeAttribute(Const.USER_NAME);
        SessionInfo.set(Const.USER_ID,null);
        SessionInfo.set(Const.USER_NAME,null);
        return "redirect:/loginPage";
    }
}
