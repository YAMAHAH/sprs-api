//package com.spsrexpress.apiproxy.controller;
//
//import com.google.common.base.Strings;
//import org.springframework.boot.autoconfigure.web.ErrorProperties;
//import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
//import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
//@RestController
//public class ErrorController extends BasicErrorController {
//
//    public ErrorController(){
//        super(new DefaultErrorAttributes(), new ErrorProperties());
//    }
//
//    private static final String PATH = "/error";
//
//    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
//        Map<String, Object> body = getErrorAttributes(request,
//                isIncludeStackTrace(request, MediaType.ALL));
//        HttpStatus status = getStatus(request);
//        if (!Strings.isNullOrEmpty((String)body.get("exception")) && body.get("exception").equals(InvalidTokenException.class.getName())){
//            body.put("status", HttpStatus.FORBIDDEN.value());
//            status = HttpStatus.FORBIDDEN;
//        }
//        return new ResponseEntity<Map<String, Object>>(body, status);
//    }
//
//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }
//}
