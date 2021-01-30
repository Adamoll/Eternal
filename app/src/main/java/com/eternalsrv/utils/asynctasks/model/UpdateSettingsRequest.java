package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class UpdateSettingsRequest extends BaseUserRequest {
    @SerializedName("radious")
    private int radious;
    @SerializedName("min_match_value")
    private int minMatchValue;
    @SerializedName("sex_choice")
    private String sexChoice;
    @SerializedName("age_range_min")
    private int ageRangeMin;
    @SerializedName("age_range_max")
    private int ageRangeMax;

    public UpdateSettingsRequest(Long userId, int radious, int minMatchValue, String sexChoice, int ageRangeMin, int ageRangeMax) {
        super(userId);
        this.radious = radious;
        this.minMatchValue = minMatchValue;
        this.sexChoice = sexChoice;
        this.ageRangeMin = ageRangeMin;
        this.ageRangeMax = ageRangeMax;
    }
}
