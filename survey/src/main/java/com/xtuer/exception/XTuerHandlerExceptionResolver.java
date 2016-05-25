package com.xtuer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class XTuerHandlerExceptionResolver implements HandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(XTuerHandlerExceptionResolver.class.getName());

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {
        String errorViewName = "error.htm";
        ModelMap model = new ModelMap();

        // 我们自己定义的异常
//        if (ex instanceof ApplicationException) {
//            ApplicationException appEx = (ApplicationException) ex;
//            errorViewName = (appEx.getErrorViewName() == null) ? errorViewName : appEx.getErrorViewName();
//            model = appEx.getModel();
//        }


        String stackTrace = ExceptionUtils.getStackTrace(ex);
        logger.info(stackTrace);

        model.addAttribute("error", ex.getMessage()); // 异常信息

        return new ModelAndView(errorViewName, model);
    }
}
