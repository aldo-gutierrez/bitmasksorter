package com.aldogg.sorter.double_.st;

public class EntityDouble1 {
    double id;
    String name;

    public double getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityDouble1(double id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "";
    }
}
