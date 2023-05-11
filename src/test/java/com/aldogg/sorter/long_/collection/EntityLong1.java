package com.aldogg.sorter.long_.collection;

public class EntityLong1 {
    long id;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityLong1(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "";
    }

}
