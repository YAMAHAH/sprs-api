package com.spsrexpress.apiproxy.utils;

import com.spsrexpress.apiproxy.exception.JsonToMapException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonToMapUtil {
    private Map<String,Object> maps=new HashMap<String, Object>();
    /**
     * 是否区分属性名大小写,默认区分
     */
    private boolean hasTransform=true;

    /**
     * 将json字符串解析为map
     * @param value
     * @throws JsonToMapException
     * @throws Exception
     */
    public JsonToMapUtil(String value) throws JsonToMapException{
        this(value,true);
    }
    /**
     * 将json字符串解析为map
     * @param value   需要解析的字符串
     * @param hasTransform  是否区分大小写,默认区分
     * @throws JsonToMapException
     * @throws Exception
     */
    public JsonToMapUtil(String value,boolean hasTransform) throws JsonToMapException {
        this.hasTransform=hasTransform;
        if(value==null||"".equals(value.trim())){
            throw new JsonToMapException("处理的字符串不能为空");
        }
        if(value.trim().indexOf('[')==0){//json数组
            JSONArray jarray=new  JSONArray(value);
            jsonArrayToMap(jarray,maps,"");
        }else{//json数据
            JSONObject jobj=new JSONObject(value);
            maps=jsonToMap(jobj);
        }
    }

    /**
     * 将json转换成map
     * @param value
     */
    private Map<String,Object> jsonToMap(JSONObject jsonObject){
        Map<String,Object> map =jsonObject.toMap();
        Map<String,Object> cloneMap=new HashMap<String, Object>(map);

        for(Map.Entry<String,Object> m:cloneMap.entrySet()){
            Object o=m.getValue();
            if(o instanceof JSONObject){
                map.put(hasTransform?m.getKey():m.getKey().toLowerCase(),jsonToMap((JSONObject)o));
            }else if(o instanceof JSONArray){
                jsonArrayToMap((JSONArray)o,map,m.getKey());
            }else{
                if(!hasTransform){
                    map.remove(m.getKey());
                    map.put(m.getKey().toLowerCase(),m.getValue());
                }
            }
        }
        return map;
    }
    /**
     * jsonArray转map
     * @param array  jsonArray
     * @param map  父map
     * @param pre  key前缀
     */
    private void jsonArrayToMap(JSONArray array,Map<String,Object> map,String pre){
        for(int i=0;i<array.length();i++){
            Object o=array.get(i);
            if(o instanceof JSONObject){
                map.put(hasTransform?pre+"["+i+"]":(pre+"["+i+"]").toLowerCase(),jsonToMap((JSONObject)o));
            }else if(o instanceof JSONArray){
                jsonArrayToMap((JSONArray)o,map,pre+"["+i+"]");
            }else{
                map.put(hasTransform?pre+"["+i+"]":(pre+"["+i+"]").toLowerCase(),o);
            }
        }
    }
    /**
     * 获取属性名对应的属性值
     * @param propertyName 属性名
     * @return
     * @throws JsonToMapException
     * @throws Exception
     */
    public <T> T getProperty(String propertyName) throws JsonToMapException{
        return getProperty(propertyName, maps);
    }
    /**
     * 从map对象中获取属性名对应的属性值
     * @param propertyName
     * @param map
     * @return
     * @throws JsonToMapException
     * @throws Exception
     */
    public <T> T getProperty(String propertyName,Map<String,Object> map) throws JsonToMapException{
        if(propertyName==null||"".equals(propertyName.trim())){
            throw new JsonToMapException("属性名不能为空.");
        }
        if(!hasTransform){
            propertyName=propertyName.toLowerCase();
        }
        Object o=map;
        String[] propertys=propertyName.split("\\.");
        for(int i=0;i<propertys.length;i++){
            if(o instanceof Map==false){
                System.out.println("未知的属性名:"+propertys[i]);
                return null;
            }
            o=((Map<String,Object>)o).get(propertys[i]);
            //JSONObject会将null值解析成字符串null，这是个坑
            if(o==null){
                return null;
            }
        }
        return (T)o;
    }

    /**
     * 获取数组对象，只能在区分大小写时使用
     * 获取数组数据目前暂时没有好的解决方案，可采用以下写法，个人觉得这样比较繁琐
     * 在不区分大小写的情况，解析后，默认的JsonArray仍然在内存中,先获取JsonArray对象
     * @param propertyName
     * @return
     * @throws JsonToMapException
     */
    public Map<String,Object> getArrayPropertys(String propertyName) throws JsonToMapException{
        JSONArray array=getProperty(propertyName);

        Map<String,Object> result=new LinkedHashMap<String, Object>();
        jsonArrayToMap(array, result, "");
        return result;
    }
    /***
     * 获取解析后的map对象
     * @return
     */
    public Map<String,Object> getAllPropertys(){
        return maps;
    }

    /**演示程序*/
//    public static void main(String[] args) {
//        //json字符串
//        String jsonstr="{code:400,pick:null,msg:\"访问成功\",result:{uname:\"测试\",level:12,good:[22,33,234,4,5],p:[{page:1,size:10},{page:2,size:20}]},arr:[[1,2,3],[4,5,6]],objarr:[[{p1:0,p2:\"二维数组中的对象1\"},{p1:999,p2:\"二维数组对象2\"}]]}";
//        //json数组字符串
//        String arraystr="[{p:123,m:\"qwer\",abc:\"呵呵呵\"},{p:1234,m:\"qwerdf\"}]";
//        try {
//            JsonToMapUtil util=new JsonToMapUtil(jsonstr);
//            //这是个null值
//            System.out.println(util.getProperty("pick")==null);
//            //获取msg
//            System.out.println("msg: "+util.getProperty("msg"));
//
//            System.out.println(util.getProperty("pick"));
//            //获取result中的uname
//            System.out.println("result.uname: "+util.getProperty("result.uname"));
//            //获取result中的good数组的第一个
//            System.out.println("result.good[0]: "+util.getProperty("result.good[0]"));
//            //获取p[0]的page
//            System.out.println("result.p[0].page: "+util.getProperty("result.p[0].page"));
//            //这是一个普通的二维数组
//            System.out.println("二维数组arr[0][0]:"+util.getProperty("arr[0][1]"));
//            //这是一个包含对象的二维数组
//            System.out.println("二维数组objarr[0][0]:"+util.getProperty("objarr[0][1].p1")+"   "+util.getProperty("objarr[0][1].p2"));
//            //获取的数组或对象均为Map<String,Object>类型
//            Map<String,Object> p=util.getProperty("result.p[0]");
//            System.out.println("result.p[0]: "+util.getProperty("size", p));
//
//            //一个不和谐的遍历数组的方法，这种方式只能遍历一维数组
//            Map<String,Object> map=util.getArrayPropertys("result.p");
//            System.out.println(map);
//            for(int i=0;i<map.size();i++){
//                System.out.println("索引"+i+"的page:"+util.getProperty("["+i+"].page",map));
//                System.out.println("索引"+i+"的size:"+util.getProperty("["+i+"].size",map));
//            }
//
//            //处理json数组类型，从[0]开始计算
//            util=new JsonToMapUtil(arraystr);
//            System.out.println(util.getProperty("[1].p"));
//            System.out.println(util.getProperty("[0].abc"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}