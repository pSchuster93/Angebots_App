package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Product;
import com.example.angebots_app.ShoppingItem;

import java.util.concurrent.CompletableFuture;

/** Task zum Aktualisieren eines Items der Einkaufsliste.
 *  Das aktualisierte Item wird zurückgegeben.
 */
public class UpdateShoppingItemTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public UpdateShoppingItemTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute (ShoppingItem shoppingItem){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean updated = crudOperations.updateShoppingItem(shoppingItem);
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
