package com.example.angebots_app;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/**
 *  Klasse zum verwalten der Datenbankzugriffe
 */
public class RoomProductCRUDOperations implements IProductCRUDOperations {

    @Dao
    public static interface RoomProductDao {
        //Products
        @Query("select * from product")
        public List<Product> readAll();

        @Insert
        public long create(Product product);

        @Update
        public int update(Product product);

        @Delete
        public void delete(Product product);

        @Query("DELETE FROM product")
        public void deleteAll();

        //Product + Offers
        @Transaction
        @Query("select * from product")
        public List<ProductWithOffers> getProductWithOffers();

        //Offers
        @Insert
        public long createOffer(Offer offer);

        @Update
        public int updateOffer(Offer offer);

        @Query("DELETE FROM offer")
        public void deleteAllOffers();

        @Query("select * from offer")
        public List<Offer> readAllOffers();

        //ShoppingItems
        @Insert
        public long createShoppingItem(ShoppingItem shoppingItem);

        @Update
        public int updateShoppingItem(ShoppingItem shoppingItem);

        @Delete
        public void deleteShoppingItem(ShoppingItem shoppingItem);

        @Query("select * from shoppingitem")
        public List<ShoppingItem> readAllShoppingItems();

        @Query("DELETE FROM shoppingitem")
        public void deleteAllShoppingItems();


    }

    @Database(entities = {Product.class, Offer.class, ShoppingItem.class},
            version = 5
    )
    public abstract static class ProductDatabase extends RoomDatabase {

        public abstract RoomProductDao getDao();

    }

    private ProductDatabase db;

    // Erstellung des Datenbankzugriffs
    public RoomProductCRUDOperations(Context context) {
        this.db = Room.databaseBuilder(context, ProductDatabase.class, "angebotsApp.db").build();
    }

    @Override
    public Product createProduct(Product product) {
        long id = db.getDao().create(product);
        product.setId(id);
        return product;
    }

    @Override
    public List<Product> readAllProducts() {
        return db.getDao().readAll();
    }

    @Override
    public Product readProduct() {
        return null;
    }

    @Override
    public boolean updateProduct(Product product) {
        if (db.getDao().update(product) > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteProduct(Product product) {
        db.getDao().delete(product);
    }

    @Override
    public void deleteAllProducts() {
        db.getDao().deleteAll();
    }

    public List<ProductWithOffers> getProductWithOffers(){
        return db.getDao().getProductWithOffers();
    }

    public Offer createOffer(Offer offer) {
        long id = db.getDao().createOffer(offer);
        offer.setId(id);
        return offer;
    }

    public boolean updateOffer(Offer offer) {
        if (db.getDao().updateOffer(offer) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteAllOffers() {
        db.getDao().deleteAllOffers();
    }

    public List<Offer> readAllOffers() {
        return db.getDao().readAllOffers();
    }

    @Override
    public ShoppingItem createShoppingItem(ShoppingItem shoppingItem) {
        long id = db.getDao().createShoppingItem(shoppingItem);
        shoppingItem.setId(id);
        return shoppingItem;
    }

    @Override
    public boolean updateShoppingItem(ShoppingItem shoppingItem) {
        if (db.getDao().updateShoppingItem(shoppingItem) > 0 ){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        db.getDao().deleteShoppingItem(shoppingItem);
    }

    @Override
    public List<ShoppingItem> readAllShoppingItems() {
        return db.getDao().readAllShoppingItems();
    }

    public void deleteAllShoppingItems() {
        db.getDao().deleteAllShoppingItems();
    }

}
