package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.ProductWithOffers;
import com.example.angebots_app.ShoppingItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReadAllShoppingItemsTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    /** Task zum Lesen aller Items der Einkaufsliste
     */
    public ReadAllShoppingItemsTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<List<ShoppingItem>> execute (){
        final CompletableFuture<List<ShoppingItem>> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ShoppingItem> shoppingItems = crudOperations.readAllShoppingItems();
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // nothing
                }
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(shoppingItems);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}