package com.cisdi.cpm.auth.common;

import com.cisdi.cpm.auth.exception.AbortException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class BaseController {
    protected ModelAndView createLayoutView(String path) {
        return createLayoutView(path, null);
    }

    protected ModelAndView createLayoutView(String path, String layout) {
        ModelAndView view = new ModelAndView();
        if (layout == null) {
            view.setViewName("common/layout");
            view.addObject("header_path", "common/header.ftl");
            view.addObject("left_path", "common/left.ftl");
            view.addObject("footer_path", "common/footer.ftl");
        } else {
            view.setViewName(layout);
            view.addObject("header_path", "common/header.ftl");
            view.addObject("left_path", "common/left.ftl");
            view.addObject("footer_path", "common/footer.ftl");
        }
        view.addObject("content_path", path);
        return view;
    }
    /**
     * handle exception msg
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler
    public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (ex instanceof AbortException) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(520);
            try {
                PrintWriter writer = response.getWriter();
                writer.write(ex.getMessage());
                writer.flush();
            } catch (IOException e) {

            }
            return null;
        }
        ex.printStackTrace();
        request.setAttribute("ex", ex);
        ModelAndView view = new ModelAndView();
        view.addObject("message", ex.getMessage());
        view.addObject("content_path", "error/error.vm");
        view.setViewName("common/layout");
        view.addObject("left_path", "common/left.vm");
        view.addObject("footer_path", "common/footer.vm");
        return view;
    }
}

