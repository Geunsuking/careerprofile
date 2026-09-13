package com.example.careerprofile.entity;

import jakarta.persistence.*;

@Entity
public class CareerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String itemValue;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CareerProfile profile;

    public CareerInfo() {
    }

    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public CareerProfile getProfile() {
        return profile;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public void setProfile(CareerProfile profile) {
        this.profile = profile;
    }
}