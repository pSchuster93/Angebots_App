package com.example.angebots_app;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.example.angebots_app.Product;

import java.io.Serializable;
import java.util.List;

/**
 * Klasse zum Mappen von Produkten und deren Angeboten
 */
public class ProductWithOffers implements Serializable {

    @Embedded public Product product;
    @Relation(
            parentColumn = "id",
            entityColumn = "productId"
    )
    public List<Offer> offers;

    public ProductWithOffers(Product product, List<Offer> offers){
        this.product = product;
        this.offers = offers;
    }

    public String getProductName(){
        return this.product.getProductName();
    }

    public int getOfferCount(){
        int i = 0;
        for(Offer o : this.offers){
            if(o.isOffer()){
                i += 1;
            }
        }
        return i;
    }

    public long getProductId(){
        return this.product.getId();
    }
}
