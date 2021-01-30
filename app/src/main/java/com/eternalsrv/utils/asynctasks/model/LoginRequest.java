package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("fb_id")
    private Long fbIdl;
    @SerializedName("accessToken")
    private String accessToken;

    public LoginRequest(Long fbIdl, String accessToken) {
        this.fbIdl = fbIdl;
        this.accessToken = accessToken;
    }
}
