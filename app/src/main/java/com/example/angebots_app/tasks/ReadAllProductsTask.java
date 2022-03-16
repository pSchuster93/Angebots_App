package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Task zum Lesen aller Produkte
 */
public class ReadAllProductsTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public ReadAllProductsTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<List<Product>> execute (){
        final CompletableFuture<List<Product>> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Product> products = crudOperations.readAllProducts();
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
