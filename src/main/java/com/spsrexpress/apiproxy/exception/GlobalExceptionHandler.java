package com.spsrexpress.apiproxy.exception;

import com.spsrexpress.apiproxy.controller.ServiceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ModelMap ioExceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("request page url{}, params:{}, error:{}", request.getRequestURI(), request.getParameterMap(), e);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("MessageCode",HttpStatus.INTERNAL_SERVER_ERROR);
        modelMap.addAttribute("MessageText",e.getMessage());
        modelMap.addAttribute("Status",ServiceCode.EXCEPTION);
        return modelMap;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ModelMap ExceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("request page url{}, params:{}, error:{}", request.getRequestURI(), request.getParameterMap(), e);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("MessageCode","");
        modelMap.addAttribute("MessageText",e.getMessage());
        modelMap.addAttribute("Status",ServiceCode.EXCEPTION);
        return modelMap;
    }

    @ExceptionHandler(value = SpsrException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ModelMap spsrExceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("request page url{}, params:{}, error:{}", request.getRequestURI(), request.getParameterMap(), e);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("MessageCode","");
        modelMap.addAttribute("MessageText",e.getMessage());
        modelMap.addAttribute("Status",ServiceCode.EXCEPTION);
        return modelMap;
    }

}
