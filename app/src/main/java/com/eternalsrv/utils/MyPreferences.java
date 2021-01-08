package com.eternalsrv.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Adam on 24-02-2017.
 */

public class MyPreferences implements Serializable {
    private Long userId;
    private Long fbId;
    private String firstName;
    private String lastName;
    private String mbtiType;
    private String imageURL;
    private String email;
    private String gender;
    private String sexChoice;
    private String QBToken;
    private String fbAccessToken;
    private String description;
    private int radious;
    private int minMatchValue;
    private int ageRangeMin;
    private int ageRangeMax;
    private double longitude;
    private double latitude;
    private Date birthday;
    private Date QBTokenExpiration;

    public MyPreferences() {
        empty();
    }

    public MyPreferences(MyPreferences pref) throws IOException, ClassNotFoundException {

        try{
            this.userId = pref.getUserId();
            this.firstName = pref.getFirstName();
            this.lastName = pref.getLastName();
            this.fbId = pref.getFbId();
            this.mbtiType = pref.getMbtiType();
            this.imageURL = pref.getImageURL();
            this.radious = pref.getRadious();
            this.minMatchValue = pref.getMinMatchValue();
            this.ageRangeMax = pref.getAgeRangeMax();
            this.ageRangeMin = pref.getAgeRangeMin();
            this.sexChoice = pref.getSexChoice();
            this.longitude = pref.getLongitude();
            this.latitude = pref.getLatitude();
            this.birthday = pref.getBirthday();
            this.QBToken = pref.getQBToken();
            this.QBTokenExpiration = pref.getQBTokenExpiration();
            this.fbAccessToken = pref.getFbAccessToken();
            this.description = pref.getDescription();
        }catch(Exception e){
            e.printStackTrace();
            this.userId = null;
            this.firstName = "";
            this.lastName = "";
            this.fbId = null;
            this.mbtiType = "";
            this.imageURL = "";
            this.radious = 50;
            this.sexChoice = "M";
            this.minMatchValue = 75;
            this.ageRangeMin = 18;
            this.ageRangeMax = 60;
            this.longitude = 0;
            this.latitude = 0;
            this.gender = "";
            this.email = "";
            this.birthday = null;
            this.QBToken = "";
            this.QBTokenExpiration = null;
            this.fbAccessToken = null;
            this.description = null;
        }
    }

    public void empty()
    {
        this.userId = null;
        this.firstName = "";
        this.lastName = "";
        this.fbId = null;
        this.mbtiType = "";
        this.imageURL = "";
        this.radious = 50;
        this.sexChoice = "M";
        this.minMatchValue = 75;
        this.longitude = 0;
        this.latitude = 0;
        this.ageRangeMin = 18;
        this.ageRangeMax = 60;
        this.gender = "";
        this.email = "";
        this.birthday = null;
        this.QBToken = "";
        this.QBTokenExpiration = null;
        this.fbAccessToken = null;
        this.description = null;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFbId() {
        return fbId;
    }

    public void setFbId(Long fbId) {
        this.fbId = fbId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMbtiType() {
        return mbtiType;
    }

    public void setMbtiType(String mbtiType) { this.mbtiType = mbtiType; }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public int getRadious() {
        return radious;
    }

    public void setRadious(int radious) {
        this.radious = radious;
    }

    public String getSexChoice() {
        return sexChoice;
    }

    public void setSexChoice(String sexChoice) {
        this.sexChoice = sexChoice;
    }

    public int getMinMatchValue() {
        return minMatchValue;
    }

    public void setMinMatchValue(int minMatchValue) {
        this.minMatchValue = minMatchValue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getQBToken() {
        return QBToken;
    }

    public void setQBToken(String QBToken) {
        this.QBToken = QBToken;
    }

    public Date getQBTokenExpiration() {
        return QBTokenExpiration;
    }

    public void setQBTokenExpiration(Date QBTokenExpiration) {
        this.QBTokenExpiration = QBTokenExpiration;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }

    public int getAgeRangeMin() {
        return ageRangeMin;
    }

    public void setAgeRangeMin(int ageRangeMin) {
        this.ageRangeMin = ageRangeMin;
    }

    public int getAgeRangeMax() {
        return ageRangeMax;
    }

    public void setAgeRangeMax(int ageRangeMax) {
        this.ageRangeMax = ageRangeMax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
