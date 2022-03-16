package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;
import com.example.angebots_app.ProductWithOffers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Task zum Lesen aller Produkte und deren zugehörigen Angeboten
 */
public class GetAllProductsAndOffersTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public GetAllProductsAndOffersTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<List<ProductWithOffers>> execute (){
        final CompletableFuture<List<ProductWithOffers>> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ProductWithOffers> products = crudOperations.getProductWithOffers();
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // nothing
                }
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(products);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
