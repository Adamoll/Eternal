package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class LoginReply extends BaseReply {
    @SerializedName("user_id")
    private Long userId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("min_match_value")
    private int minMatchValue;
    @SerializedName("sex_choice")
    private String sexChoice;
    private int radius;
    @SerializedName("age_range_min")
    private int ageRangeMin;
    @SerializedName("age_range_max")
    private int ageRangeMax;
    private String description;
    private String type;

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getMinMatchValue() {
        return minMatchValue;
    }

    public String getSexChoice() {
        return sexChoice;
    }

    public int getRadius() {
        return radius;
    }

    public int getAgeRangeMin() {
        return ageRangeMin;
    }

    public int getAgeRangeMax() {
        return ageRangeMax;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}
