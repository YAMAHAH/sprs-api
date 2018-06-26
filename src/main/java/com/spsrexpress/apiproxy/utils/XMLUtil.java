package com.spsrexpress.apiproxy.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class XMLUtil {

    private static void JsonToMap(Stack<JSONObject> stObj, Map<String, Object> resultMap)
            throws  IOException {

        if(stObj == null && stObj.pop() == null){
            return ;
        }
        JSONObject json = stObj.pop();
        Iterator it = json.keys();
        while(it.hasNext()){
            String key = (String) it.next();
            //得到value的值
            Object value = json.get(key);

            if(value instanceof JSONObject)
            {
                stObj.push((JSONObject)value);
                //递归遍历
                JsonToMap(stObj,resultMap);
            }
            else {
                resultMap.put(key, value);
            }
//            if (entry.getValue() == null || NULL.equals(entry.getValue())) {
//                value = null;
//            } else if (entry.getValue() instanceof JSONObject) {
//                value = ((JSONObject) entry.getValue()).toMap();
//            } else if (entry.getValue() instanceof JSONArray) {
//                value = ((JSONArray) entry.getValue()).toList();
//            } else {
//                value = entry.getValue();
//            }
//            results.put(entry.getKey(), value);
        }
    }
    public JSONObject xmlToJSONObject(String xml){
        JSONObject jsonObject = XML.toJSONObject(xml);
        return jsonObject;
    }

    public Map<String, Object> xmlToMap(String xml) throws IOException {
        JSONObject jsonObject = XML.toJSONObject(xml);
        Stack<JSONObject> stObj = new Stack<JSONObject>();
        stObj.push(jsonObject);
        Map<String, Object> resultMap =new HashMap<>();
        JsonToMap(stObj,resultMap);
        return resultMap;
    }

    public  Object getValue(JSONObject jsonObject, String path){
        String[] paths = path.split(".");
        JSONObject objValue = jsonObject;
        for (String p:paths) {
            objValue =  objValue.getJSONObject(p);
        }
        return objValue;
    }

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
