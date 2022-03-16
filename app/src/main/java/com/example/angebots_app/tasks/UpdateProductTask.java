package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;

import java.util.concurrent.CompletableFuture;

/** Task zum Aktualisieren eines Produkts.
 *  Das aktualisierte Produkt wird zurückgegeben.
 */
public class UpdateProductTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public UpdateProductTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute (Product product){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean updated = crudOperations.updateProduct(product);
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(updated);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
