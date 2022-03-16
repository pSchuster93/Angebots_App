package com.example.angebots_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Klasse, welche die ShoppingItem-Tabelle repräsentiert
 */
@Entity
public class ShoppingItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long productId;
    private long offerId;
    private boolean isDone;

    public ShoppingItem(long productId, long offerId, boolean isDone) {
        this.productId = productId;
        this.offerId = offerId;
        this.isDone = isDone;
    }
     public ShoppingItem(){

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

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingItem that = (ShoppingItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
