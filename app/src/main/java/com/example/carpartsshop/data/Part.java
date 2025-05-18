package com.example.carpartsshop.data;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Part implements Serializable {
    @DocumentId
    private String id;
    private String brand;
    private int year;
    private String model;
    private String name;
    private String imageUrl;
    private double price;
    private int stock;
    private String description;

    public Part() { }

    public Part(String id,
                String brand,
                int year,
                String model,
                String name,
                String imageUrl,
                double price,
                int stock,
                String description) {
        this.id          = id;
        this.brand       = brand;
        this.year        = year;
        this.model       = model;
        this.name        = name;
        this.imageUrl    = imageUrl;
        this.price       = price;
        this.stock       = stock;
        this.description = description;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
