package com.cs499.ryan.everyalcoholpercentage;

/**
 * Created by Ryan on 5/14/17.
 */

public class Beverage {

    private Long id;
    private String name;
    private String makerName;
    private String alcoholPercentage;

    public Beverage() {

    }

    public Beverage(Long id, String name, String makerName, String alcoholPercentage) {
        this.id = id;
        this.name = name;
        this.makerName = makerName;
        this.alcoholPercentage = alcoholPercentage;
    }

    public String getName() {
        return name;
    }

    public String getMakerName() {
        return makerName;
    }

    public String getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public Long getId() { return id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public void setAlcoholPercentage(String alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }
}
