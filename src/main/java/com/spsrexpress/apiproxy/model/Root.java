/**
  * Copyright 2018 bejson.com 
  */
package com.spsrexpress.apiproxy.model;

import java.util.Date;

/**
 * Auto-generated: 2018-06-17 10:59:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Root {

    private Date xmlns;
    private Params Params;
    private Login Login;
    private XmlConverter XmlConverter;

    public void setXmlns(Date xmlns) {
        this.xmlns = xmlns;
    }

    public Date getXmlns() {
        return xmlns;
    }

    public void setParams(Params Params) {
        this.Params = Params;
    }

    public Params getParams() {
        return Params;
    }

    public void setLogin(Login Login) {
        this.Login = Login;
    }

    public Login getLogin() {
        return Login;
    }

    public void setXmlConverter(XmlConverter XmlConverter) {
        this.XmlConverter = XmlConverter;
    }

    public XmlConverter getXmlConverter() {
        return XmlConverter;
    }

}