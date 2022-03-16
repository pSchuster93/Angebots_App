package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Offer;
import com.example.angebots_app.Product;
import com.example.angebots_app.ShoppingItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Task zum Lesen aller Angebote.
 */
public class ReadAllOffersTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public ReadAllOffersTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<List<Offer>> execute (){
        final CompletableFuture<List<Offer>> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Offer> offers = crudOperations.readAllOffers();
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // nothing
                }
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(offers);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
