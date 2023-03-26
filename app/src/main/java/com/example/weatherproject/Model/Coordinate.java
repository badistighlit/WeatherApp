package com.example.weatherproject.Model;

public class Coordinate extends Location{
    private int lat;
    private int lon;

    public Coordinate(int lat, int lon){
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    public int getLat() {
        return lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }
}
