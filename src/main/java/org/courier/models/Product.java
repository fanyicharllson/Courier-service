package org.courier.models;


public class Product {
    private int productId;
    private String name;
    private String description;
    private double price;
    private String imagePath;
    private int likes;
    private int quantity;

    public Product(int id, String name, String description, double price, String imagePath, int likes, int quantity) {
        this.productId = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.likes = likes;
        this.quantity = quantity;
    }

    public int getId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
    public int getLikes() { return likes; }
    public int getQuantity() { return quantity; }
}
