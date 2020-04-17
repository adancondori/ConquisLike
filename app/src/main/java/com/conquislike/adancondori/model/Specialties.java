package com.conquislike.adancondori.model;

import java.io.Serializable;

public class Specialties implements Serializable {
    private Long id;
    public String name;
    public String description;
    public String filename;
    public String imagename;
    public Long user_id;
    public String created_at;

    public boolean exist = false;
    public String buttonName = "DESCARGAR";

    public void setExist(boolean exist) {
        this.exist = exist;
        this.buttonName = "VER PDF";
    }
}
