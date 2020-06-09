package com.cisdi.cpm.auth.utils;

import com.cisdi.cpm.auth.common.contant.Const;

import javax.servlet.http.HttpSession;

public class SessionUtils {
    public static void putUserId(HttpSession session, String userId) {
        session.setAttribute(Const.USER_ID, userId);
    }
    public static String getUserId(HttpSession session) {
        return (String) session.getAttribute(Const.USER_ID);
    }

    public static void putUserName(HttpSession session, String userName) {
        session.setAttribute(Const.USER_NAME, userName);
    }
    public static String getUserName(HttpSession session) {
        return (String) session.getAttribute(Const.USER_NAME);
    }
}
