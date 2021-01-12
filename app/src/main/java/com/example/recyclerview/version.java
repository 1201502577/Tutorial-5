package com.example.recyclerview;

public class version {
    private String name;
    private String description;
    private String icon;

    public version(String name, String description,int icon){
        this.name = name;
        this.description = description;
        this.icon = icon;

    }

    public version(String name, String description, String icon) {
    }

    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;

    }
    public String getIcon(){
        return icon;
    }
}
