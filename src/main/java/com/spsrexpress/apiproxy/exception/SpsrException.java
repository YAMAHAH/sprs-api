package com.spsrexpress.apiproxy.exception;

public class SpsrException extends Exception {
    private static final long serialVersionUID = 6959850410521968829L;
    public SpsrException(String msg) {
        super("连接异常:" + msg);
    }
}
