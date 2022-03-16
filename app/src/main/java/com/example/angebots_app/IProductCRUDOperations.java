package com.example.angebots_app;

import java.util.List;

/**
 * Interface, welches die Methoden für den Datenbankzugriff definiert
 */
public interface IProductCRUDOperations {
    public Product createProduct(Product product);

    public List<Product> readAllProducts();

    public Product readProduct();

    public boolean updateProduct(Product product);

    public void deleteProduct(Product product);

    public void deleteAllProducts();

    public List<ProductWithOffers> getProductWithOffers();

    public Offer createOffer(Offer offer);

    public List<Offer> readAllOffers();

    public boolean updateOffer(Offer offer);

    public void deleteAllOffers();

    public ShoppingItem createShoppingItem(ShoppingItem shoppingItem);

    public boolean updateShoppingItem (ShoppingItem shoppingItem);

    public void deleteShoppingItem(ShoppingItem shoppingItem);

    public List<ShoppingItem> readAllShoppingItems();

    public void deleteAllShoppingItems();
}
