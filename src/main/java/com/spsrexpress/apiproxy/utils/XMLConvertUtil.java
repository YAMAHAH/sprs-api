package com.spsrexpress.apiproxy.utils;

import org.json.JSONObject;
import org.json.XML;

import java.util.Map;

public class XMLConvertUtil {

    public static String xmlToJson(String xml){
        //将xml转为json
        JSONObject xmlJSONObj = XML.toJSONObject(xml);
        //设置缩进
        String jsonPrettyPrintString = xmlJSONObj.toString(4);
        return  jsonPrettyPrintString;
    }

    public static String jsonToXML(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return XML.toString(jsonObject);
    }

    public static String jsonToXML(Map<String,Object> root) {
        JSONObject jsonObject = new JSONObject(root);
        return XML.toString(jsonObject);
    }
}
