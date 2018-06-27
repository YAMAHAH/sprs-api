package com.spsrexpress.apiproxy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ModelMap errorHandler(HttpServletRequest request, Exception e) {
        logger.error("request page url{}, params:{}, error:{}", request.getRequestURI(), request.getParameterMap(), e);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("message",e.getMessage());
        modelMap.addAttribute("status","系统异常");
        return modelMap;
    }

}
