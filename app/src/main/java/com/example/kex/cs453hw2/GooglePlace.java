package com.example.kex.cs453hw2;

/**
 * Created by kex on 10/23/17.
 */

public class GooglePlace {
    private String name;
    private String lat;
    private String lng;

    public GooglePlace() {
        this.name = "";
        lat = "";
        lng = "";
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLat() {
        return lat;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLng() {
        return lng;
    }
}