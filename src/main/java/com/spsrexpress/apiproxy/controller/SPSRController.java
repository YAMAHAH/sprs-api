package com.spsrexpress.apiproxy.controller;

import com.spsrexpress.apiproxy.utils.HttpRequestUtil;
import com.spsrexpress.apiproxy.utils.XMLConvertUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/spsr/v1/")
public class SPSRController {

    @Autowired
    private Environment env;

    @Autowired
    private XMLConvertUtil xmlConvertUtil;

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

    /**
     * 可以直接使用@ResponseBody响应JSON
     *
     * @param request
     * @param response
     * @return
     */
    @ApiIgnore
    @ResponseBody
    @RequestMapping(value = "/jsonTest", method = RequestMethod.POST)
    public ModelMap jsonTest(HttpServletRequest request,
                             HttpServletResponse response) {
        ModelMap map = new ModelMap();
        map.addAttribute("hello", "你好");
        map.addAttribute("veryGood", "很好");
        return map;
    }

    @ApiOperation(value = "用户登录",notes = "获取登录用户的SID")
    @PostMapping(path = "/waLogin",produces = "application/json")
    public String login() throws IOException{
        String xml = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/login/1.0\">\n"
                + "<p:Params Name=\"WALogin\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n"
                + "<Login  Login=\"" + username + "\" Pass=\"" + password +  "\" UserAgent=\"Company name\" />\n" + "</root>";

        String res =httpRequestUtil.postRequest(getRequestUrl(), xml);
        String jsonStr = xmlConvertUtil.xmlToJson(res);

        return jsonStr;
    }

    @ApiOperation(value="注销SID", notes="注销指定的SID")
    @PostMapping(path="/waLogout",produces = "application/json")
    public String logout(@RequestParam String sId) throws IOException{
        String reqParam = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/logout/1.0\" >\n" +
                " <p:Params Name=\"WALogout\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n" +
                " <Logout Login=\" " + username +  "\" SID=\" " + sId + "\" /> </root>";

        String xmlRes = httpRequestUtil.postRequest(getRequestUrl(),reqParam);
        return xmlConvertUtil.xmlToJson(xmlRes);
    }

    @ApiOperation(value="获取快递单信息",notes = "根据指定的请求数据获取快递单的相关信息")
    @PostMapping(path = "/waGetInvoiceInfo",produces = "application/json")
    public String waGetInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                       @RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = xmlConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @ApiOperation(value = "获取快递单跟踪信息",notes = "根据指定的请求参数获取快递单的跟踪信息")
    @PostMapping(path = "/wAMonitorInvoiceInfo",produces = "application/json")
    public String waMonitorInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                            @RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = xmlConvertUtil.xmlToJson(res);
        return jsonStr;
    }

    @ApiOperation(value = "创建并激活快递单",notes = "根据指定的请求参数创建并激活快递单")
    @PostMapping(value = "/waCreateInvoice",produces = MediaType.APPLICATION_JSON_VALUE)
    public String createInvoice(@ApiParam(name = "reqParam", value = "JSON或XML格式的参数数据", required = true)
                                     @RequestBody String reqParam, HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        String res="{ \"result\": \"0\" }";
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = xmlConvertUtil.jsonToXML(reqParam);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return xmlConvertUtil.xmlToJson(res);
    }

    @ApiOperation(value = "Json转换XML",notes = "Json格式转换到XML格式的接口方法")
    @PostMapping(value = "/waJsonToXml",produces = MediaType.APPLICATION_XML_VALUE)
    public String jsonToXml(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                @RequestBody String reqParam){
        return xmlConvertUtil.jsonToXML(reqParam);
    }

    @ApiOperation(value = "XML转换JSON",notes = "XML转换到JSON格式的接口方法")
    @PostMapping(value = "/waXmlToJson",produces = MediaType.APPLICATION_JSON_VALUE)
    public String xmlToJson(@ApiParam(name = "reqParam", value = "XML格式的参数数据", required = true)
                                @RequestBody String reqParam){
        return xmlConvertUtil.xmlToJson(reqParam);
    }

    @ApiOperation(value = "执行SPSR API接口",notes = "根据请求的参数,执行SPSR API接口方法")
    @PostMapping(value = "/waExecuteApi",produces = MediaType.APPLICATION_JSON_VALUE)
    public String executeApi(@ApiParam(name = "reqParam", value = "JSON或XML格式的参数数据", required = true)
                                 @RequestBody String reqParam, HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        String res="{ \"result\": \"0\" }";
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = xmlConvertUtil.jsonToXML(reqParam);
            reqXml= reqXml.replace("p_Params","p:Params").replace("xmlns_p","xmlns:p");
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return xmlConvertUtil.xmlToJson(res);
    }

}
