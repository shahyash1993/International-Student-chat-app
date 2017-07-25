package com.example.yps.assignment_5;

/**
 * Created by yPs on 4/23/2017.
 */

public class LatiLongi {

    double lati,longi;

    public LatiLongi(double lati, double longi) {
        this.lati = lati;
        this.longi = longi;
    }

    public LatiLongi() {
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
