package com.spsrexpress.apiproxy;

import com.google.common.base.Strings;
import com.spsrexpress.apiproxy.utils.HttpRequestUtil;
import com.spsrexpress.apiproxy.utils.SecureIdStore;
import com.spsrexpress.apiproxy.utils.XMLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ApplicatonTask {

    @Autowired
    private XMLUtil xmlUtil;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    private String httpsTestUrl = "https://api.spsr.ru/test";
    private String httpsProdUrl = "https://api.spsr.ru/";

    private String httpTestUrl = "http://api.spsr.ru:8020/waExec/WAExec";
    private String httpProdUrl = "http://api.spsr.ru/waExec/WAExec";

    @Value("${spsr.transport.protocol}")

    private String transportProtocol;
    @Value("${spsr.login.username}")
    private String username;
    @Value("${spsr.login.password}")
    private String password;

    @Value("${spsr.execute.mode}")
    private String envMode;

    private Boolean isRetry = false;

    @Scheduled(cron = "0 0 06 ? * *" )
    public void updateSecureIdTask() throws IOException {
        String sid = SecureIdStore.getLoginSecureId();
        String oldSid = sid;
        String xmlRes = loginRequest();
        Map<String, Object> map = xmlUtil.xmlToMap(xmlRes);
        sid = (String) map.get("_SID");
        if (!Strings.isNullOrEmpty(sid)) {
            SecureIdStore.setOldLoginSecureId(oldSid);
            SecureIdStore.setLoginSecureId(sid);
            isRetry = false;
        }else{
            isRetry = true;
        }
    }

    @Scheduled(fixedRate = 5000 )
    public void reUpdateSecureIdTask() throws IOException {
        if(isRetry){
            String sid = SecureIdStore.getLoginSecureId();
            String oldSid = sid;
            String xmlRes = loginRequest();
            Map<String, Object> map = xmlUtil.xmlToMap(xmlRes);
            sid = (String) map.get("_SID");
            if (!Strings.isNullOrEmpty(sid)) {
                SecureIdStore.setOldLoginSecureId(oldSid);
                SecureIdStore.setLoginSecureId(sid);
                isRetry = false;
            }
        }

    }

    private String logoutRequest(String sId) throws IOException{
        String reqParam = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/logout/1.0\" >\n" +
                " <p:Params Name=\"WALogout\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n" +
                " <Logout Login=\" " + username +  "\" SID=\" " + sId + "\" /> </root>";

        String xmlRes = httpRequestUtil.postRequest(getRequestUrl(),reqParam);
        return reqParam;
    }

    private String loginRequest() throws IOException {
        String xml = "<root xmlns=\"http://spsr.ru/webapi/usermanagment/login/1.0\">\n"
                + "<p:Params Name=\"WALogin\" Ver=\"1.0\" xmlns:p=\"http://spsr.ru/webapi/WA/1.0\" />\n"
                + "<Login  Login=\"" + username + "\" Pass=\"" + password +  "\" UserAgent=\"Company name\" />\n" + "</root>";

        String res =httpRequestUtil.postRequest(getRequestUrl(), xml);
        return res;
    }

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
}
