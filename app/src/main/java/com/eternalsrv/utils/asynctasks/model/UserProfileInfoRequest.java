package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class UserProfileInfoRequest extends BaseUserRequest {
    @SerializedName("user_profile_ids")
    private String userProfileIds;

    public UserProfileInfoRequest(Long userId, String userProfileIds) {
        super(userId);
        this.userProfileIds = userProfileIds;
    }
}
