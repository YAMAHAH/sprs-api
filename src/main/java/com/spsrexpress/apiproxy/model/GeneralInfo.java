/**
  * Copyright 2018 bejson.com 
  */
package com.spsrexpress.apiproxy.model;
import java.util.List;

/**
 * Auto-generated: 2018-06-17 10:59:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class GeneralInfo {

    private String ContractNumber;
    private List<Invoice> Invoice;

    public void setContractNumber(String ContractNumber) {
        this.ContractNumber = ContractNumber;
    }

    public String getContractNumber() {
        return ContractNumber;
    }

    public void setInvoice(List<Invoice> Invoice) {
        this.Invoice = Invoice;
    }

    public List<Invoice> getInvoice() {
        return Invoice;
    }

}