package com.example.angebots_app;

import androidx.room.*;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Klasse, welche die Produkt-Tabelle repräsentiert
 */
@Entity
public class Product implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "product_size")
    private String productSize;

    private boolean favorite;

    @ColumnInfo(name = "description")
    private String productDescription;

    private int kcal;

    private double carbs;

    private double fat;

    private double protein;

    private double fibers;

    private String category;

    public Product() {
    }

    public Product(String productName) {
        this.productName = productName;
    }

    public Product(String productName, String productSize, boolean favorite, String productDescription, int kcal, double carbs, double fat, double protein, double fibers, String category) {
        this.productName = productName;
        this.productSize = productSize;
        this.favorite = favorite;
        this.productDescription = productDescription;
        this.kcal = kcal;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.fibers = fibers;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFibers() {
        return fibers;
    }

    public void setFibers(double fibers) {
        this.fibers = fibers;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getResourceSrc(){
        switch (this.productName) {
            case "Coca Cola" :
                return R.drawable.cola;
            case "Milka Erdbeer" :
                return R.drawable.milka;
            case "Teekanne Organics Happy Time" :
                return R.drawable.teekanne_happy_time;
            case "Teekanne Fresh Pfirsich" :
                return R.drawable.teekanne_fresh;
            default:
                return R.drawable.ic_close_black_56dp;
        }
    }
}
