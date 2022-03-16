package com.example.angebots_app;

/**
 * Hilfsklasse zum Halten eines Produkts und eines Angebots aus den ShoppingItems
 */
public class ProductAndOffer {
    private Offer offer;
    private Product product;
    private long shoppingItemId;

    public ProductAndOffer(Product product, Offer offer, long shoppingItemId){
        this.product = product;
        this.offer = offer;
        this.shoppingItemId = shoppingItemId;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getShoppingItemId() {
        return shoppingItemId;
    }

    public void setShoppingItemId(long shoppingItemId) {
        this.shoppingItemId = shoppingItemId;
    }
}
