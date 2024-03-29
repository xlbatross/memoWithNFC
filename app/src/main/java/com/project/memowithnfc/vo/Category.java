package com.project.memowithnfc.vo;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;

    public Category() { this.id = 0; }

    public Category(String name) {
        this.name = name;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
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
}
