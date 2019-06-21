package com.project.memowithnfc.vo;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String nfc;

    public Category() { this.nfc = "0"; }

    public Category(String name) {
        this.name = name;
        this.nfc = "0";
    }

    public Category(int id, String name, String nfc) {
        this.id = id;
        this.name = name;
        this.nfc = nfc;
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

    public String getNfc() { return nfc; }

    public void setNfc(String nfc) { this.nfc = nfc; }
}
