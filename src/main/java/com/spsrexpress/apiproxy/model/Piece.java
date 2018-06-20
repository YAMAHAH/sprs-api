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
public class Piece {

    private String Description;
    private String ClientBarcode;
    private String Weight;
    private String Length;
    private String Width;
    private String Depth;
    private SubPiece SubPiece;

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setClientBarcode(String ClientBarcode) {
        this.ClientBarcode = ClientBarcode;
    }

    public String getClientBarcode() {
        return ClientBarcode;
    }

    public void setWeight(String Weight) {
        this.Weight = Weight;
    }

    public String getWeight() {
        return Weight;
    }

    public void setLength(String Length) {
        this.Length = Length;
    }

    public String getLength() {
        return Length;
    }

    public void setWidth(String Width) {
        this.Width = Width;
    }

    public String getWidth() {
        return Width;
    }

    public void setDepth(String Depth) {
        this.Depth = Depth;
    }

    public String getDepth() {
        return Depth;
    }

    public void setSubPiece(SubPiece SubPiece) {
        this.SubPiece = SubPiece;
    }

    public SubPiece getSubPiece() {
        return SubPiece;
    }

}