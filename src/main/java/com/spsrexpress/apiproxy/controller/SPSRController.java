package com.spsrexpress.apiproxy.controller;

import com.google.common.base.Strings;
import com.spsrexpress.apiproxy.exception.SpsrException;
import com.spsrexpress.apiproxy.utils.HttpRequestUtil;
import com.spsrexpress.apiproxy.utils.MapUtil;
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
    @PostMapping(path = "/waGetInvoiceInfoByNative",produces = "application/json")
    public String waGetInvoiceInfoByNative(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                       @RequestBody String reqParam,HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res="{ \"result\": \"0\" }";
        res = getInvoiceInfoString(reqParam, contentType, res);
        String jsonStr = xmlUtil.xmlToJson(res);
        return jsonStr;
    }

    @ApiOperation(value="获取快递单信息",notes = "根据指定的请求数据获取快递单的相关信息")
    @PostMapping(path = "/waGetInvoiceInfo",produces = "application/json")
    public ModelMap waGetInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                           @RequestBody String reqParam,HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res="{ \"result\": \"0\" }";
        res = getInvoiceInfoString(reqParam, contentType, res);
        Map<String,Object> map = xmlUtil.xmlToMap(res);
        //获取指定keys的所有值
//        String[] keys = { "_RC","_MessageCode","_MessageInfo","_Status","_ShipRefNum","_ShipmentNumber",
//        "_ErrorCode","_ErrorMessage"};
//
//        Map<String,Object> mapValue =  MapUtil.getValueToMap(keys,map);

        String rc = map.get("_RC").toString();
        if(Strings.isNullOrEmpty(rc) || rc.equalsIgnoreCase("0")){
            String messageCode = map.get("_MessageCode") != null ? map.get("_MessageCode").toString() : "";
            String messageInfo = map.get("_MessageInfo") != null ? map.get("_MessageInfo").toString() : "";
            String status = map.get("_Status") != null ? map.get("_Status").toString() : "";
            String gcNumber = map.get("_ShipRefNum") != null ? map.get("_ShipRefNum").toString() : "";
            String invoiceNumber = map.get("_ShipmentNumber") !=null ?  map.get("_ShipmentNumber").toString() : "";
            String errorCode = map.get("_ErrorCode") != null ? map.get("_ErrorCode").toString() : "";
            String errorMessage = map.get("_ErrorMessage") != null ? map.get("_ErrorMessage").toString() : "";
            if(!Strings.isNullOrEmpty(messageCode)) {
                String msgText = getMessageText(messageCode);
                modelMap.addAttribute("MessageCode", messageCode );
                modelMap.addAttribute("MessageInfo", messageInfo);
                modelMap.addAttribute("MessageText", msgText);
            }else if ( !Strings.isNullOrEmpty(errorCode)){
                String msgText = getMessageText(errorCode);
                modelMap.addAttribute("MessageCode", errorCode );
                modelMap.addAttribute("MessageText", msgText);
            }
            else {
                JSONArray messages = map.get("Message") != null ?(JSONArray) map.get("Message") :null;
                if(messages != null){
                    modelMap.addAttribute("MessageText",messages);
//                    for (Object msg:messages.toList()) {
//
//                    }
                }
            }
            modelMap.addAttribute("ResultCode",rc);
            modelMap.addAttribute("Status",ServiceCode.QUERY);

            modelMap.addAttribute("GCNumber",gcNumber);
            modelMap.addAttribute("InvoiceNumber",invoiceNumber);
        }else{
            String errMsg = getError(rc);
            modelMap.addAttribute("MessageCode",rc);
            modelMap.addAttribute("Status",ServiceCode.ERROR);
            modelMap.addAttribute("MessageText",errMsg);
        }
        //String jsonStr = xmlUtil.xmlToJson(res);
        return modelMap;
    }

    private String getInvoiceInfoString(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true) @RequestBody String reqParam, String contentType, String res) throws IOException {
        if( contentType.equalsIgnoreCase("application/xml")){
            res = httpRequestUtil.postRequest(getRequestUrl(), getInvoiceQureyXml(getLoginSecureId() ,this.username,this.contractNumber,reqParam));
        }else if(contentType.equalsIgnoreCase("application/json")){
            String reqXml = xmlUtil.jsonToXML(reqParam);
            reqXml = getInvoiceQureyXml(getLoginSecureId(),this.username,contractNumber,reqXml);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return res;
    }

    @ApiOperation(value = "获取快递单跟踪信息",notes = "根据指定的请求参数获取快递单的跟踪信息")
    @PostMapping(path = "/waMonitorInvoiceInfoByNative",produces = "application/json")
    public String waMonitorInvoiceInfoByNative(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                         @RequestBody String reqParam, HttpServletRequest request) throws IOException {
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res = "{ \"result\": \"0\" }";
        res = getMonitorInvoiceInfoString(reqParam, contentType, res);
        String jsonStr = xmlUtil.xmlToJson(res);
        return jsonStr;
    }

    private String getMonitorInvoiceInfoString(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true) @RequestBody String reqParam, String contentType, String res) throws IOException {
        if (contentType.equalsIgnoreCase("application/xml")) {
            res = httpRequestUtil.postRequest(getRequestUrl(), getMonitorInvoiceInfoXmlParam(getLoginSecureId(), this.contractNumber, reqParam));
        } else if (contentType.equalsIgnoreCase("application/json")) {
            String reqXml = xmlUtil.jsonToXML(reqParam);
            reqXml = getMonitorInvoiceInfoXmlParam(getLoginSecureId(), contractNumber, reqXml);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return res;
    }

    @ApiOperation(value = "获取快递单跟踪信息",notes = "根据指定的请求参数获取快递单的跟踪信息")
    @PostMapping(path = "/waMonitorInvoiceInfo",produces = "application/json")
    public ModelMap waMonitorInvoiceInfo(@ApiParam(name = "reqParam", value = "JSON格式的参数数据", required = true)
                                            @RequestBody String reqParam, HttpServletRequest request) throws IOException{
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res="{ \"result\": \"0\" }";
        res = getMonitorInvoiceInfoString(reqParam, contentType, res);
        // String jsonStr = xmlUtil.xmlToJson(res);
       // return jsonStr;
        Map<String,Object> map = xmlUtil.xmlToMap(res);
        String rc = map.get("_RC").toString();
        if(Strings.isNullOrEmpty(rc) || rc.equalsIgnoreCase("0")){
            String messageCode = MapUtil.getValueAsString("_MessageCode",map);  //map.get("_MessageCode") != null ? map.get("_MessageCode").toString() : "";
            String messageInfo = MapUtil.getValueAsString("_MessageInfo",map);// map.get("_MessageInfo") != null ? map.get("_MessageInfo").toString() : "";
            String status =MapUtil.getValueAsString("_Status",map); // map.get("_Status") != null ? map.get("_Status").toString() : "";
            String gcNumber =MapUtil.getValueAsString("_GCInvoiceNumber",map); // map.get("_GCInvoiceNumber") != null ? map.get("_GCInvoiceNumber").toString() : "";
            String invoiceNumber =MapUtil.getValueAsString("_InvoiceNumber",map); // map.get("_InvoiceNumber") !=null ?  map.get("_InvoiceNumber").toString() : "";
            String errorCode = MapUtil.getValueAsString("_ErrorCode",map); // map.get("_ErrorCode") != null ? map.get("_ErrorCode").toString() : "";
            if(!Strings.isNullOrEmpty(messageCode)) {
                String msgText = getMessageText(messageCode);
                modelMap.addAttribute("MessageCode", messageCode );
                modelMap.addAttribute("MessageInfo", messageInfo);
                modelMap.addAttribute("MessageText", msgText);
            }else {
                JSONArray messages = map.get("Message") != null ?(JSONArray) map.get("Message") :null;
                if(messages != null){
                    modelMap.addAttribute("MessageText",messages);
//                    for (Object msg:messages.toList()) {
//
//                    }
                }
            }
            modelMap.addAttribute("ResultCode",rc);
            modelMap.addAttribute("Status",ServiceCode.QUERY);

            modelMap.addAttribute("GCNumber",gcNumber);
            modelMap.addAttribute("InvoiceNumber",invoiceNumber);
        }else{
            String errMsg = getError(rc);
            modelMap.addAttribute("MessageCode",rc);
            modelMap.addAttribute("Status",ServiceCode.ERROR);
            modelMap.addAttribute("MessageText",errMsg);
        }
        return modelMap;
    }



    private String getCreateInvoiceString(@RequestBody String reqParam, String contentType, String res) throws IOException {
        if (contentType.equalsIgnoreCase("application/xml")) {
            res = httpRequestUtil.postRequest(getRequestUrl(), getCreateInvoiceAction(getLoginSecureId(), contractNumber, reqParam));
        } else if (contentType.equalsIgnoreCase("application/json")) {
            String reqXml = xmlUtil.jsonToXML(reqParam);
            reqXml = getCreateInvoiceAction(getLoginSecureId(), contractNumber, reqXml);
            res = httpRequestUtil.postRequest(getRequestUrl(), reqXml);
        }
        return res;
    }

    @ApiOperation(value = "创建并激活快递单",notes = "根据指定的请求参数创建并激活快递单")
    @PostMapping(value = "/waCreateInvoice",produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelMap createInvoice(@ApiParam(name = "reqParam", value = "JSON或XML格式的参数数据", required = true)
                                     @RequestBody String reqParam, HttpServletRequest request) throws IOException {
        String contentType = request.getHeader("Content-Type").toLowerCase();

        ModelMap modelMap = new ModelMap();
        String res = "{ \"result\": \"0\" }";
        res = getCreateInvoiceString(reqParam, contentType, res);
        Map<String, Object> map = xmlUtil.xmlToMap(res);
        String rc = map.get("_RC").toString();
        if (Strings.isNullOrEmpty(rc) || rc.equalsIgnoreCase("0")) {
            String messageCode = map.get("_MessageCode") != null ? map.get("_MessageCode").toString() : "";
            String messageInfo = map.get("_MessageInfo") != null ? map.get("_MessageInfo").toString() : "";
            String status = map.get("_Status") != null ? map.get("_Status").toString() : "";
            String gcNumber = map.get("_GCNumber") != null ? map.get("_GCNumber").toString() : "";
            String invoiceNumber = map.get("_InvoiceNumber") != null ? map.get("_InvoiceNumber").toString() : "";
            if (!Strings.isNullOrEmpty(messageCode)) {
                String msgText = getMessageText(messageCode);
                modelMap.addAttribute("MessageCode", messageCode);
                modelMap.addAttribute("MessageInfo", messageInfo);
                modelMap.addAttribute("MessageText", msgText);
            } else {
                JSONArray messages = map.get("Message") != null ? (JSONArray) map.get("Message") : null;
                if (messages != null) {
                    modelMap.addAttribute("MessageText", messages);
//                    for (Object msg:messages.toList()) {
//
//                    }
                }
            }
            modelMap.addAttribute("ResultCode", rc);
            if (status.equalsIgnoreCase("Rejected")) {
                modelMap.addAttribute("Status", ServiceCode.REJECTED);
            } else if (status.equalsIgnoreCase("Created")) {
                modelMap.addAttribute("Status", ServiceCode.CREATED);
            } else {
                modelMap.addAttribute("Status", ServiceCode.UPDATED);
            }
            modelMap.addAttribute("GCNumber", gcNumber);
            modelMap.addAttribute("InvoiceNumber", invoiceNumber);
        } else {
            String errMsg = getError(rc);
            modelMap.addAttribute("MessageCode", rc);
            modelMap.addAttribute("Status", ServiceCode.ERROR);
            modelMap.addAttribute("MessageText", errMsg);
        }
        return modelMap;
    }
    @PostMapping(value = "/waCreateInvoiceByNative",produces = MediaType.APPLICATION_JSON_VALUE)
    private String createInvoiceByNative(@RequestBody String reqParam, HttpServletRequest request) throws IOException {
        String contentType = request.getHeader("Content-Type").toLowerCase();
        String res = "{ \"result\": \"0\" }";
        res = getCreateInvoiceString(reqParam, contentType, res);
        return xmlUtil.xmlToJson(res);
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
            case "1006":
                errMsg = "会话已经过期或不存在";
                break;
        }

        return errMsg;
    }

    private String getMonitorInvoiceInfoXmlParam(String sid,String icn,String dataBody) {
        String rootXml = "<root xmlns=\"http://spsr.ru/webapi/Monitoring/MonInvoiceInfo/1.3\">\n" +
                " <p:Params Name=\"WAMonitorInvoiceInfo\" Ver=\"1.3\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n" +
                "<Login SID=\"" + sid + "\" ICN=\"" + icn + "\" />\n" +
                "<Monitoring Language=\"en\" >\n" + dataBody + "\n" +
                "</Monitoring> </root>";
        return rootXml;
    }


    private String getInvoiceQureyXml(String sid,String username,String icn,String dataBody){
        String rootXml = "<root xmlns=\"http://spsr.ru/webapi/DataEditManagment/GetInvoiceInfo/1.1\"> \n" +
                "<p:Params Name=\"WAGetInvoiceInfo\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" Ver=\"1.1\"/> \n" +
                "<Login SID=\"" +sid +  "\" Login=\"" + username +  "\" ICN=\"" + icn +  "\"/>\n" +
                dataBody + "\n" +
                "</root>\n";
        return rootXml;
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
