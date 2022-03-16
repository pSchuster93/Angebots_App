package com.example.angebots_app.tasks;

import android.app.Activity;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Offer;
import com.example.angebots_app.Product;

import java.util.concurrent.CompletableFuture;

/** Task zum Erstellen eines Angebots.
    Das erstellte Angebot wird zurückgegeben
 */
public class CreateOfferTask {
    private IProductCRUDOperations crudOperations;
    private Activity owner;

    public CreateOfferTask(IProductCRUDOperations crudOperations, Activity owner) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Offer> execute (Offer offer){
        CompletableFuture<Offer> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Offer createdOffer = crudOperations.createOffer(offer);
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(createdOffer);
                    }
                });
            }
        }).start();

        return resultFuture;
    }
}
