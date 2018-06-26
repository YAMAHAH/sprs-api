package com.spsrexpress.apiproxy.utils;

import java.util.HashMap;
import java.util.Map;

public class SecureIdStore {

    private final static String secureIdKey = "spsr.login.secureid";
    private final static String oldSecureIdKey = "spsr.login.oldsecureid";
    private final  static Map<String,String> dataMaps = new HashMap<>();

    public static String getLoginSecureId(){
        if(dataMaps.containsKey(secureIdKey)) return dataMaps.get(secureIdKey);
        String uId =""; //SecureIdPropertiesUtil.getKeyValue(secureIdKey);
        return uId;
    }

    public static String getOldLoginSecureId(){
        if(dataMaps.containsKey(oldSecureIdKey)) return dataMaps.get(oldSecureIdKey);
        String uId = SecureIdPropertiesUtil.getKeyValue(oldSecureIdKey);
        return uId;
    }

    public static void setLoginSecureId(String value){
        if(dataMaps.containsKey(secureIdKey)){
            dataMaps.replace(secureIdKey,value);
        }else {
            dataMaps.put(secureIdKey,value);
        }
       // SecureIdPropertiesUtil.writeProperties(secureIdKey,value);
    }

    public static void setOldLoginSecureId(String value){
        if(dataMaps.containsKey(oldSecureIdKey)){
            dataMaps.replace(oldSecureIdKey,value);
        }else {
            dataMaps.put(oldSecureIdKey,value);
        }
       // SecureIdPropertiesUtil.writeProperties(oldSecureIdKey,value);
    }
}
