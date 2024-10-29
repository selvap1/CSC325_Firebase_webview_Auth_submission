package com.example.csc325_firebase_webview_auth.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
    private final SimpleStringProperty name;
    private final SimpleStringProperty major;
    private final SimpleIntegerProperty age;
    private StringProperty imageURL;
    private String id; // Unique identifier for each person

    public Person(String id, String name, String major, int age, String imageURL) {
        this.id = id; // Initialize id here
        this.name = new SimpleStringProperty(name);
        this.major = new SimpleStringProperty(major);
        this.age = new SimpleIntegerProperty(age);
        this.imageURL = new SimpleStringProperty(imageURL);
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for name
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Getter and setter for major
    public String getMajor() {
        return major.get();
    }

    public void setMajor(String major) {
        this.major.set(major);
    }

    public StringProperty majorProperty() {
        return major;
    }

    // Getter and setter for age
    public int getAge() {
        return age.get();
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public SimpleIntegerProperty ageProperty() {
        return age;
    }

    // Getter and Setter for imageURL
    public String getImageURL() {
        return imageURL.get();
    }

    public void setImageURL(String imageURL) {
        this.imageURL.set(imageURL);
    }

    public StringProperty imageURLProperty() {
        return imageURL;
    }
}
