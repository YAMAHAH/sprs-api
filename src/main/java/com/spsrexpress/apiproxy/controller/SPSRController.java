package com.spsrexpress.apiproxy.controller;

import com.google.common.base.Strings;
import com.spsrexpress.apiproxy.exception.SpsrException;
import com.spsrexpress.apiproxy.utils.HttpRequestUtil;
import com.spsrexpress.apiproxy.utils.SecureIdStore;
import com.spsrexpress.apiproxy.utils.XMLUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spsr/v1/")
public class SPSRController {

    @Autowired
    private Environment env;

    @Autowired
    private XMLUtil xmlUtil;

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

    @Value("${spsr.login.secureid}")
    private String secureId;

    @Value("${spsr.contractnumber}")
    private String contractNumber;

    private String getLoginSecureId() throws IOException {
       String sid = SecureIdStore.getLoginSecureId();
       if(Strings.isNullOrEmpty(sid)){
           String xmlRes = login();
           Map<String,Object> map = xmlUtil.xmlToMap(xmlRes);
           //错误信息处理
           sid =(String)map.get("_SID");
           if(!Strings.isNullOrEmpty(sid)){
               SecureIdStore.setLoginSecureId(sid);
           }else{
               sid = "";
           }
       }
       return sid;
    }

    private String login() throws IOException{
        String xml = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/login/1.0\">\n"
                + "<p:Params Name=\"WALogin\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n"
                + "<Login  Login=\"" + username + "\" Pass=\"" + password +  "\" UserAgent=\"Company name\" />\n" + "</root>";

        String res =httpRequestUtil.postRequest(getRequestUrl(), xml);
        return res;
    }

    @PostMapping(path="/waLogin",produces = "application/json")
    public ModelMap waLogin() throws IOException{
        ModelMap map = new ModelMap();
        String SID = getLoginSecureId();
        if(Strings.isNullOrEmpty(SID)){
            map.addAttribute("status","error");
            map.addAttribute("mssage","获取SID失败");
        }else{
            map.addAttribute("status","succeed");
            map.addAttribute("SID",SID);
        }
        return map;
    }

    @ApiOperation(value="获取快递单信息",notes = "根据指定的请求数据获取快递单的相关信息")
    @PostMapping(path = "/waGetInvoiceInfo",produces = "application/json")
    public String waGetInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                       @RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = xmlUtil.xmlToJson(res);
        return jsonStr;
    }

    @ApiOperation(value = "获取快递单跟踪信息",notes = "根据指定的请求参数获取快递单的跟踪信息")
    @PostMapping(path = "/wAMonitorInvoiceInfo",produces = "application/json")
    public String waMonitorInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                            @RequestBody String reqParam) throws IOException{
        String res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        String jsonStr = xmlUtil.xmlToJson(res);
        return jsonStr;
    }

    @ApiOperation(value = "创建并激活快递单",notes = "根据指定的请求参数创建并激活快递单")
    @PostMapping(value = "/waCreateInvoice2",produces = MediaType.APPLICATION_JSON_VALUE)
    public String createInvoice2(@ApiParam(name = "reqParam", value = "JSON或XML格式的参数数据", required = true)
                                @RequestBody String reqParam, HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        String res="{ \"result\": \"0\" }";
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), reqParam);
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = xmlUtil.jsonToXML(reqParam);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return xmlUtil.xmlToJson(res);
    }

    @ApiOperation(value = "创建并激活快递单",notes = "根据指定的请求参数创建并激活快递单")
    @PostMapping(value = "/waCreateInvoice",produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelMap createInvoice(@ApiParam(name = "reqParam", value = "JSON或XML格式的参数数据", required = true)
                                     @RequestBody String reqParam, HttpServletRequest request) throws IOException {
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res="{ \"result\": \"0\" }";
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), getCreateInvoiceAction(getLoginSecureId(),contractNumber,reqParam));
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = xmlUtil.jsonToXML(reqParam);
            reqXml = getCreateInvoiceAction(getLoginSecureId(),contractNumber,reqXml);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        System.out.println(res);
        Map<String,Object> map = xmlUtil.xmlToMap(res);
        String rc = map.get("_RC").toString();
        System.out.println(rc);
        if(Strings.isNullOrEmpty(rc) || rc.equalsIgnoreCase("0")){
            String messageCode = map.get("_MessageCode") != null ? map.get("_MessageCode").toString() : "";
            String messageInfo = map.get("_MessageInfo") != null ? map.get("_MessageInfo").toString() : "";
            String status = map.get("_Status") != null ? map.get("_Status").toString() : "";
            String gcNumber = map.get("_GCNumber") != null ? map.get("_GCNumber").toString() : "";
            String invoiceNumber = map.get("_InvoiceNumber") !=null ?  map.get("_InvoiceNumber").toString() : "";
            if(!Strings.isNullOrEmpty(messageCode)) {
                String msgText = getMessageText(messageCode);
                modelMap.addAttribute("messageCode", messageCode);
                modelMap.addAttribute("messageInfo", messageInfo);
                modelMap.addAttribute("messageText", msgText);
            }else {
                JSONArray messages = map.get("Message") != null ?(JSONArray) map.get("Message") :null;
                if(messages != null){
                    modelMap.addAttribute("messageText",messages);
//                    for (Object msg:messages.toList()) {
//
//                    }
                }
            }
            modelMap.addAttribute("resultcode",rc);
            if(status.equalsIgnoreCase("Rejected")){
                modelMap.addAttribute("status","已拒绝");
            }else if(status.equalsIgnoreCase("Created")){
                modelMap.addAttribute("status","已创建");
            }else{
                modelMap.addAttribute("status","已更新");
            }
            modelMap.addAttribute("GCNumber",gcNumber);
            modelMap.addAttribute("InvoiceNumber",invoiceNumber);
        }else{
            String errMsg = getError(rc);
            modelMap.addAttribute("errorcode",rc);
            modelMap.addAttribute("status","error");
            modelMap.addAttribute("message",errMsg);
        }
        return modelMap;
    }

    private String getMessageText(String messageCode){
        switch (messageCode){
            case "DUP":
                return "字段内容重复";
            case "SUB":
                return "SUB";
            case "CNF":
                return "CNF";
            case "DAT":
                return "DAT";
            case "CHN":
                return "CHN";
            case "EMP":
                return "EMP";
            case "UPR":
                return "UPR";
                default:
                    return "未知字段错误";
        }
    }

    private String getError(String errorCode) {
        String errMsg = "连接服务器超时";
        switch (errorCode){
            case "1007":
                errMsg = "会话已经过期或不存在";
                break;
        }

        return errMsg;
    }

    private String getCreateInvoiceAction(String sId,String icn,String dataBody) {
        String rootXml = "<root xmlns=\"http://spsr.ru/webapi/xmlconverter/1.3\">\n" +
                " <Params Name=\"WAXmlConverter\" Ver=\"1.3\" xmlns=\"http://spsr.ru/webapi/WA/1.0\" />\n" +
                " <Login SID=\"" + sId + "\"/>\n" +
                " <XmlConverter> \n" +
                " <GeneralInfo ContractNumber=\"" + icn + "\">\n" +
                "\n" +
                dataBody + "\n" +
                " </GeneralInfo>\n" +
                "</XmlConverter>\n" +
                "</root>";
        return rootXml;
    }

    @ApiOperation(value = "Json转换XML",notes = "Json格式转换到XML格式的接口方法")
    @PostMapping(value = "/waJsonToXml",produces = MediaType.APPLICATION_XML_VALUE)
    public String jsonToXml(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                @RequestBody String reqParam){
        return xmlUtil.jsonToXML(reqParam);
    }

    @ApiOperation(value = "XML转换JSON",notes = "XML转换到JSON格式的接口方法")
    @PostMapping(value = "/waXmlToJson",produces = MediaType.APPLICATION_JSON_VALUE)
    public String xmlToJson(@ApiParam(name = "reqParam", value = "XML格式的参数数据", required = true)
                                @RequestBody String reqParam){
        return xmlUtil.xmlToJson(reqParam);
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
            String reqXml = xmlUtil.jsonToXML(reqParam);
            reqXml= reqXml.replace("p_Params","p:Params").replace("xmlns_p","xmlns:p");
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return xmlUtil.xmlToJson(res);
    }

}
