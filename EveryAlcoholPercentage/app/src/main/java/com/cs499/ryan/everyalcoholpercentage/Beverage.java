package com.cs499.ryan.everyalcoholpercentage;

import java.util.Arrays;

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

    public int hashCode() {
        Object[] x = {id, name, makerName, alcoholPercentage};
        return Arrays.hashCode(x);
    }
    public String toString() {
        return name;
    }
}
