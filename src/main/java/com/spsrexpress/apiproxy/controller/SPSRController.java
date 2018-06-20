package com.spsrexpress.apiproxy.controller;

import com.spsrexpress.apiproxy.utils.HttpRequestUtil;
import com.spsrexpress.apiproxy.utils.XMLConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
@RequestMapping("/spsr")
public class SPSRController {

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    private String httpsTestUrl = "https://api.spsr.ru/test";
    private String httpsProdUrl = "https://api.spsr.ru/";

    private String httpTestUrl = "http://api.spsr.ru:8020/waExec/WAExec";
    private String httpProdUrl = "http://api.spsr.ru/waExec/WAExec";

    private String getRequestUrl(){
        Boolean isHttps = transportProtocol.equalsIgnoreCase("https");
        String reqUrl = "";
        if(this.envMode.equalsIgnoreCase("test")){
            reqUrl = isHttps ?  httpsTestUrl : httpTestUrl;
        }else{
            reqUrl = isHttps ? httpsProdUrl : httpProdUrl;
        }
        return reqUrl;
    }

    @Value("${spsr.execute.mode}")
    private String envMode;

    @Value("${spsr.transport.protocol}")
    private String transportProtocol;

    @Value("${spsr.login.username}")
    private String username;
    @Value("${spsr.login.password}")
    private String password;

    @PostMapping(path = "/waLogin",produces = "application/json")
    public String login() throws IOException{
        String xml = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/login/1.0\">\n"
                + "<p:Params Name=\"WALogin\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n"
                + "<Login  Login=\"" + username + "\" Pass=\"" + password +  "\" UserAgent=\"Company name\" />\n" + "</root>";

        String res =httpRequestUtil.postRequest(getRequestUrl(), xml);
        String jsonStr = XMLConvertUtil.xmlToJson(res);

        return jsonStr;
    }

    @PostMapping(path="waLogout",produces = "application/json")
    public String logout(@RequestParam String sId) throws IOException{
        String reqParam = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/logout/1.0\" >\n" +
                " <p:Params Name=\"WALogout\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n" +
                " <Logout Login=\" " + username +  "\" SID=\" " + sId + "\" /> </root>";

        String xmlRes = httpRequestUtil.postRequest(getRequestUrl(),reqParam);
        return XMLConvertUtil.xmlToJson(xmlRes);
    }

    @PostMapping(path = "/waGetSpsrOffices",produces = "application/json")
    public String wAGetSpsrOffices(@RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = XMLConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @PostMapping(path = "/wAGetStreet",produces = "application/json")
    public String WAGetStreet(@RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = XMLConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @PostMapping(path = "/getCities",produces = "application/json")
    public String getCities(@RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(),reqParam);
        String jsonStr = XMLConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @PostMapping(path = "/waGetInvoiceInfo",produces = "application/json")
    public String wAGetInvoiceInfo(@RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = XMLConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @PostMapping(value = "/waCreateInvoice",produces = MediaType.APPLICATION_JSON_VALUE)
    public String createInvoice(@RequestBody String reqParam, HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        String res="{ \"result\": \"0\" }";
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = XMLConvertUtil.jsonToXML(reqParam);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return XMLConvertUtil.xmlToJson(res);
    }

}
