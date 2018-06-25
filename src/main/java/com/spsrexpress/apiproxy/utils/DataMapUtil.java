package com.spsrexpress.apiproxy.utils;

import java.util.HashMap;
import java.util.Map;

public class DataMapUtil {

    private final  static Map<String,String> dataMaps = new HashMap<>();

    public static String getData(String key){
        if(dataMaps.containsKey(key)) return dataMaps.get(key);
        String uId = UserLoginUtil.getKeyValue("spsr.login.sid");
        return uId;
    }

    public static void setData(String key,String value){
        if(dataMaps.containsKey(key)){
            dataMaps.replace(key,value);
        }else {
            dataMaps.put(key,value);
        }
        UserLoginUtil.writeProperties(key,value);
    }
}
