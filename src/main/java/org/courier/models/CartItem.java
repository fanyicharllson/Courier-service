package org.courier.models;

public class CartItem {
    private int productId;
    private String name;
    private double price;
    private int quantity;
    private String imagePath;


    public CartItem(int productId, String name, double price, int quantity, String imagePath) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }
}
