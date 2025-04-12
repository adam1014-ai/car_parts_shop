package com.example.carpartsshop;

public class User {
    private String firstName;
    private String lastName;
    private String address;
    private String email;

    public User() {
        // Required for Firebase
    }

    public User(String firstName, String lastName, String address, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}
