package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdateRequest extends BaseUserRequest {
    private String description;
    @SerializedName("photo_links")
    private String photoLinks;

    public ProfileUpdateRequest(Long userId, String description, String photoLinks) {
        super(userId);
        this.description = description;
        this.photoLinks = photoLinks;
    }
}
