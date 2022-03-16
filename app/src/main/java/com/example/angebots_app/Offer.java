package com.example.angebots_app;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Klasse, welche die Angebot-Tabelle repräsentiert
 */
@Entity
public class Offer implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long productId;

    private String shopName;

    private double price;

    private boolean isOffer;

    public Offer(long productId, String shopName, double price, boolean isOffer) {
        this.productId = productId;
        this.shopName = shopName;
        this.price = price;
        this.isOffer = isOffer;
    }

    public Offer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean offer) {
        isOffer = offer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return id == offer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getResourceSrc(){
        switch (this.shopName) {
            case "Rewe" :
                return R.drawable.rewe_logo;
            case "Lidl" :
                return R.drawable.lidl_logo;
            default:
                return R.drawable.ic_close_black_56dp;
        }
    }
}
