package com.example.myapplication;

public class Brick {
    private String id;
    private String name;
    private String type;
    private String color;
    private double weight;
    private double price;
    private boolean inStock;
    private String createdAt;

    public Brick() {}

    public Brick(String name, String type, String color, double weight, double price) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.weight = weight;
        this.price = price;
        this.inStock = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}