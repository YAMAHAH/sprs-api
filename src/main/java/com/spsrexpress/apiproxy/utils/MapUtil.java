package com.spsrexpress.apiproxy.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static String getValueAsString(String key, Map<String, Object> map) {
        Object value = map.get(key);
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    public static Map<String, Object> getValueToMap(String[] keys, Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                value = value.toString();
            } else {
                value = "";
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }
}
