/**
  * Copyright 2018 bejson.com 
  */
package com.spsrexpress.apiproxy.model;

/**
 * Auto-generated: 2018-06-17 10:59:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Invoice {

    private String Action;
    private String ShipRefNum;
    private String PickUpType;
    private String ProductCode;
    private String FullDescription;
    private String PiecesCount;
    private String InsuranceType;
    private String InsuranceSum;
    private String CODGoodsSum;
    private String CODDeliverySum;
    private String CODDeliveryVAT;
    private Receiver Receiver;
    private AdditionalServices AdditionalServices;
    private Pieces Pieces;

    public void setAction(String Action) {
        this.Action = Action;
    }

    public String getAction() {
        return Action;
    }

    public void setShipRefNum(String ShipRefNum) {
        this.ShipRefNum = ShipRefNum;
    }

    public String getShipRefNum() {
        return ShipRefNum;
    }

    public void setPickUpType(String PickUpType) {
        this.PickUpType = PickUpType;
    }

    public String getPickUpType() {
        return PickUpType;
    }

    public void setProductCode(String ProductCode) {
        this.ProductCode = ProductCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setFullDescription(String FullDescription) {
        this.FullDescription = FullDescription;
    }

    public String getFullDescription() {
        return FullDescription;
    }

    public void setPiecesCount(String PiecesCount) {
        this.PiecesCount = PiecesCount;
    }

    public String getPiecesCount() {
        return PiecesCount;
    }

    public void setInsuranceType(String InsuranceType) {
        this.InsuranceType = InsuranceType;
    }

    public String getInsuranceType() {
        return InsuranceType;
    }

    public void setInsuranceSum(String InsuranceSum) {
        this.InsuranceSum = InsuranceSum;
    }

    public String getInsuranceSum() {
        return InsuranceSum;
    }

    public void setCODGoodsSum(String CODGoodsSum) {
        this.CODGoodsSum = CODGoodsSum;
    }

    public String getCODGoodsSum() {
        return CODGoodsSum;
    }

    public void setCODDeliverySum(String CODDeliverySum) {
        this.CODDeliverySum = CODDeliverySum;
    }

    public String getCODDeliverySum() {
        return CODDeliverySum;
    }

    public void setCODDeliveryVAT(String CODDeliveryVAT) {
        this.CODDeliveryVAT = CODDeliveryVAT;
    }

    public String getCODDeliveryVAT() {
        return CODDeliveryVAT;
    }

    public void setReceiver(Receiver Receiver) {
        this.Receiver = Receiver;
    }

    public Receiver getReceiver() {
        return Receiver;
    }

    public void setAdditionalServices(AdditionalServices AdditionalServices) {
        this.AdditionalServices = AdditionalServices;
    }

    public AdditionalServices getAdditionalServices() {
        return AdditionalServices;
    }

    public void setPieces(Pieces Pieces) {
        this.Pieces = Pieces;
    }

    public Pieces getPieces() {
        return Pieces;
    }

}