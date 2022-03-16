package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;

import java.util.concurrent.CompletableFuture;

/** Task zum Löschen aller Produkte.
 */
public class DeleteAllProductsTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public DeleteAllProductsTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute (){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                crudOperations.deleteAllProducts();
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(true);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
