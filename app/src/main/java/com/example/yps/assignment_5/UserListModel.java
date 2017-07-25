package com.example.yps.assignment_5;

import java.io.Serializable;

/**
 * Created by yPs on 4/15/2017.
 */

public class UserListModel implements Serializable {

    String nickname_adp, year_adp, stateName_adp, countryName_adp;

    public UserListModel(String nickname_adp, String countryName_adp, String stateName_adp,  String year_adp) {
        this.nickname_adp = nickname_adp;
        this.year_adp = year_adp;
        this.stateName_adp = stateName_adp;
        this.countryName_adp = countryName_adp;
    }

    public String getNickname_adp() {
        return nickname_adp;
    }

    public void setNickname_adp(String nickname_adp) {
        this.nickname_adp = nickname_adp;
    }

    public String getYear_adp() {
        return year_adp;
    }

    public void setYear_adp(String year_adp) {
        this.year_adp = year_adp;
    }

    public String getStateName_adp() {
        return stateName_adp;
    }

    public void setStateName_adp(String stateName_adp) {
        this.stateName_adp = stateName_adp;
    }

    public String getCountryName_adp() {
        return countryName_adp;
    }

    public void setCountryName_adp(String countryName_adp) {
        this.countryName_adp = countryName_adp;
    }
}

