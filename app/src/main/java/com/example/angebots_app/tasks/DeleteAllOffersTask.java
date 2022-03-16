package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;

import java.util.concurrent.CompletableFuture;

/** Task zum Löschen aller Angebote.
 */
public class DeleteAllOffersTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public DeleteAllOffersTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute (){
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                crudOperations.deleteAllOffers();
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
