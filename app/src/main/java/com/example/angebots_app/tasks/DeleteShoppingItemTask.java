package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.ShoppingItem;

import java.util.concurrent.CompletableFuture;

/** Task zum Löschen eines Items der Einkaufsliste.
 *  Nach der Durchführung wird true zurückgegeben.
 */
public class DeleteShoppingItemTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public DeleteShoppingItemTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute (ShoppingItem shoppingItem){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                crudOperations.deleteShoppingItem(shoppingItem);
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
