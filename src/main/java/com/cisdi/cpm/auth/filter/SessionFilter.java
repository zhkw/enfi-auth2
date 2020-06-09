package com.cisdi.cpm.auth.filter;

import com.cisdi.cpm.auth.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String spath = request.getRequestURI();
        //不需要过滤的url
        String[] urls = {"/login","/json",".js",".css",".ico",".jpg",".png","/loginPage"};
        //�û�ID
        String userId = SessionUtils.getUserId(request.getSession());
        //�û���
        String userName = SessionUtils.getUserName(request.getSession());

        //��Session��Ϣ����ThreadLocal
//		SessionInfo.set(Const.USER_ID, userId);
//		SessionInfo.set(Const.USER_NAME, userName);
        boolean flag = true;
        for (String str : urls) {
            if (spath.contains(str)) {
                flag =false;
                break;
            }
        }
        if (flag) {
            if (userId == null || userName == null) {
                response.sendRedirect(request.getContextPath()+ "/loginPage");
            }else {
                filterChain.doFilter(request, response);
            }
        }else{
            filterChain.doFilter(request, response);
        }

    }

}
