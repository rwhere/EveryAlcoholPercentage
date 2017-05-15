package com.cs499.ryan.everyalcoholpercentage;

/**
 * Created by Ryan on 5/14/17.
 */

public class Beverage {

    private String name;
    private String makerName;
    private double alcoholPercentage;
    private boolean favorite;

    public Beverage() {

    }

    public Beverage(String name, String makerName, double alcoholPercentage, boolean favorite) {
        this.name = name;
        this.makerName = makerName;
        this.alcoholPercentage = alcoholPercentage;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public String getMakerName() {
        return makerName;
    }

    public double getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public void setAlcoholPercentage(double alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
