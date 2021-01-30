package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class SwipeUserRequest extends BaseUserRequest {
    @SerializedName("swiped_id")
    private int swipedId;
    @SerializedName("wanna_meet")
    private int wannaMeet;
    private String name;
    @SerializedName("match_value")
    private int matchValue;

    public SwipeUserRequest(Long userId, int swipedId, int wannaMeet, String name, int matchValue) {
        super(userId);
        this.swipedId = swipedId;
        this.wannaMeet = wannaMeet;
        this.name = name;
        this.matchValue = matchValue;
    }
}
