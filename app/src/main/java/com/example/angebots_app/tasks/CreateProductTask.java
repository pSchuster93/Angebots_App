package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;

import java.util.concurrent.CompletableFuture;

/** Task zum Erstellen eines Produkts.
    Das erstellte Produkt wird zurückgegeben
 */
public class CreateProductTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public CreateProductTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Product> execute (Product product){
        CompletableFuture<Product> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Product createdProduct = crudOperations.createProduct(product);
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(createdProduct);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
