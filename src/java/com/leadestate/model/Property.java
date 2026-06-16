/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

public class Property {

    private int id;
    private String name;
    private String location;
    private float price;

    // KONSTRUKTOR
    public Property() {
    }

    public Property(int id, String name, String location, float price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
    }

    public Property(String name, String location, float price) {
        this.name = name;
        this.location = location;
        this.price = price;
    }

    // METHOD (sesuai class diagram)

    public void updatePrice(double newPrice) {
        if (newPrice <= 0) {
            System.out.println("[Property] Harga baru tidak valid: " + newPrice
                    + ". Harga harus lebih dari 0.");
            return;
        }
        double hargaLama = this.price;
        this.price = (float) newPrice;
        System.out.println("[Property] Harga properti id=" + this.id
                + " diperbarui dari Rp " + hargaLama + " menjadi Rp " + newPrice);
    }

    // GETTER & SETTER

    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    // OVERRIDE toString()

    @Override
    public String toString() {
        return "Property{"
                + "id=" + this.id
                + ", name='" + this.name + "'"
                + ", location='" + this.location + "'"
                + ", price=" + this.price
                + "}";
    }
}
