package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Offer;
import com.example.angebots_app.ShoppingItem;

import java.util.concurrent.CompletableFuture;

/** Task zum Erstellen eines Items für die Einkaufsliste.
    Das erstellte Item wird zurückgegeben
 */
public class CreateShoppingItemTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public CreateShoppingItemTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<ShoppingItem> execute (ShoppingItem shoppingItem){
        CompletableFuture<ShoppingItem> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ShoppingItem createdShoppingItem = crudOperations.createShoppingItem(shoppingItem);
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(createdShoppingItem);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
