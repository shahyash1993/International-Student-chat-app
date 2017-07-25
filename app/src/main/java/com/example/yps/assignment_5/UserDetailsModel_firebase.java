package com.example.yps.assignment_5;

/**
 * Created by yPs on 4/22/2017.
 */

public class UserDetailsModel_firebase {

    String selectedCountry, selectedState, nickname, password, city, id;
    int year;
    double lat = 0, lng = 0;
    public UserDetailsModel_firebase userDetailsModel_firebase;


    public UserDetailsModel_firebase() {
    }

    public UserDetailsModel_firebase(String selectedCountry, String selectedState, String nickname, String password, String city, String id, int year, double lat, double lng, UserDetailsModel_firebase userDetailsModel_firebase) {
        this.selectedCountry = selectedCountry;
        this.selectedState = selectedState;
        this.nickname = nickname;
        this.password = password;
        this.city = city;
        this.id = id;
        this.year = year;
        this.lat = lat;
        this.lng = lng;
        this.userDetailsModel_firebase = userDetailsModel_firebase;
    }

    public UserDetailsModel_firebase(String selectedCountry, String selectedState, String nickname, String password, String city, String id, int year, double lat, double lng) {
        this.selectedCountry = selectedCountry;
        this.selectedState = selectedState;
        this.nickname = nickname;
        this.password = password;
        this.city = city;
        this.id = id;
        this.year = year;
        this.lat = lat;
        this.lng = lng;
    }

    public UserDetailsModel_firebase getUserDetailsModel_firebase() {
        return userDetailsModel_firebase;
    }

    public void setUserDetailsModel_firebase(UserDetailsModel_firebase userDetailsModel_firebase) {
        this.userDetailsModel_firebase = userDetailsModel_firebase;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
