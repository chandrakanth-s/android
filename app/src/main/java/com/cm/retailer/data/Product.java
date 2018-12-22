package com.cm.retailer.data;

import android.text.TextUtils;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.com_cm_retailer_data_ProductRealmProxy;

@Parcel(implementations = {  com_cm_retailer_data_ProductRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Product.class })
public class Product extends RealmObject{
    @PrimaryKey
    String id;
    String productName;
    String productCode;
    String productSummary;
    String quantity;
    String kind;
    String unitsOfKind;
    String price;
    Date createdDate = new Date();
    Date modifiedDate = new Date();
    @Ignore
    public boolean isSelected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductSummary() {
        return productSummary;
    }

    public void setProductSummary(String productSummary) {
        this.productSummary = productSummary;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind.getValue();
    }

    public String getUnitsOfKind() {
        return unitsOfKind;
    }

    public void setUnitsOfKind(String unitsOfKind) {
        this.unitsOfKind = unitsOfKind;
    }

    public String getKindInformation(){
        String info = "1 "+kind;
        if(unitsOfKind!=null && !TextUtils.isEmpty(unitsOfKind)){
            info = info+", "+unitsOfKind+" Units";
        }
        return info;
    }
}
