package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class ProfileDeletePhotoRequest extends BaseUserRequest {
    @SerializedName("qb_token")
    private String qbToken;
    @SerializedName("photo_link")
    private String photoLink;
    @SerializedName("blob_id")
    private Integer blobId;

    public ProfileDeletePhotoRequest(Long userId, String qbToken, String photoLink, Integer blobId) {
        super(userId);
        this.qbToken = qbToken;
        this.photoLink = photoLink;
        this.blobId = blobId;
    }
}
