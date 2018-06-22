package com.spsrexpress.apiproxy.utils;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class XMLConvertUtil {

    public String xmlToJson(String xml){
        JSONObject xmlJSONObj = XML.toJSONObject(xml);
        String jsonPrettyPrintString = xmlJSONObj.toString(4);
        return  jsonPrettyPrintString;
    }

    public String xmlToJsonV2(String xml){
        JSONObject json = new JSONObject(xml);
        return XML.toString(json);
    }

    public String jsonToXML(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return XML.toString(jsonObject);
    }

    public String jsonToXML(Map<String,Object> root) {
        JSONObject jsonObject = new JSONObject(root);
        return XML.toString(jsonObject);
    }
}
