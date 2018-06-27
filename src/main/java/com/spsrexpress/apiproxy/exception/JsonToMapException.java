package com.spsrexpress.apiproxy.exception;

public class JsonToMapException extends Exception{

    private static final long serialVersionUID = 6959850410521968827L;

    public JsonToMapException(String msg){
        super("JsonToMapUtil Exception[JsonToMapUtil拒绝了您的操作，抛出异常]:"+msg);
    }
}
